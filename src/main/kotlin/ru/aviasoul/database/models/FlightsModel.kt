package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.aviasoul.database.dto.AirportDTO
import ru.aviasoul.database.dto.FlightDTO
import ru.aviasoul.features.flights.models.AirportResponse
import ru.aviasoul.features.flights.models.FlightResponse
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object FlightsModel : Table("flights") {
    val id = varchar("id", 100)
    val departureAirportId = varchar("departure_airport_id", 100).references(AirportsModel.id)
    val arrivedAirportId = varchar("arrival_airport_id", 100).references(AirportsModel.id)
    val departureDate = varchar("departure_date_time", 50)
    val arrivalDate = varchar("arrival_date_time", 50)
    val departureTime = varchar("departure_time", 10)
    val arrivalTime = varchar("arrival_time", 10)

    override val primaryKey = PrimaryKey(id)

    fun getFlightsWithFilters(
        departureCity: String? = null,
        arrivedCity: String? = null,
        departureDt: String? = null,
        arrivedDt: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
    ): List<FlightResponse> {
        var flightsList: MutableList<FlightDTO> = mutableListOf()

        var airportsFrom: List<AirportDTO> = listOf()
        var airportsWhere: List<AirportDTO> = listOf()

        if (departureCity != null) {
            airportsFrom = AirportsModel.getAirportsByLocation(location = departureCity)
        }

        if (arrivedCity != null) {
            airportsWhere = AirportsModel.getAirportsByLocation(location = arrivedCity)
        }

        FlightsModel.selectAll().forEach {
            flightsList.add(
                FlightDTO(
                    id = it[FlightsModel.id],
                    departureAirportId = it[FlightsModel.departureAirportId],
                    arrivedAirportId = it[FlightsModel.arrivedAirportId],
                    departureDate = it[FlightsModel.departureDate],
                    arrivalDate = it[FlightsModel.arrivalDate],
                    departureTime = it[FlightsModel.departureTime],
                    arrivalTime = it[FlightsModel.arrivalTime],
                )
            )
        }

        if (departureCity != null) {
            for (airport in airportsFrom) {
                flightsList = flightsList.filter { it.departureAirportId == airport.id }.toMutableList()
            }
        }

        if (arrivedCity != null) {
            for (airport in airportsWhere) {
                flightsList = flightsList.filter { it.arrivedAirportId == airport.id }.toMutableList()
            }
        }

        if (departureDt != null) {
            flightsList = flightsList.filter {
                val depatureDateTimeInFlight = it.departureDate.split('-')
                val requiredDateArray = departureDt.split('.')

                depatureDateTimeInFlight.containsAll(requiredDateArray)
            }.toMutableList()
        }

        if (arrivedDt != null) {
            flightsList = flightsList.filter {
                val arrivalDateTimeInFlight = it.arrivalDate.split('-')
                val requiredDateArray = arrivedDt.split('.')

                val cond = arrivalDateTimeInFlight.containsAll(requiredDateArray)
                cond
            }.toMutableList()
        }

        val flightResponse: MutableList<FlightResponse> = mutableListOf()

        flightsList.forEach {
            val departureAirport = AirportsModel.getAirportById(id = it.departureAirportId)
            val arrivalAirport = AirportsModel.getAirportById(id = it.arrivedAirportId)
            val flightTickets = TicketsModel.getTicketsByFlightId(
                flightId = it.id,
                maxPrice = maxPrice,
                minPrice = minPrice
            )
            val businessPrice = flightTickets.firstOrNull{ it.type == "business" }?.price
            val defaultPrice = flightTickets.firstOrNull { it.type == "default" }?.price
            val defaultTicketsCount = flightTickets.filter { it.type == "default" }.size
            val businessTicketsCount = flightTickets.filter { it.type == "business" }.size

            flightResponse.add(
                FlightResponse(
                    id = it.id,
                    departureAirport = AirportResponse(
                        name = departureAirport.name,
                        location = departureAirport.location,
                    ),
                    arrivalAirport = AirportResponse(
                        name = arrivalAirport.name,
                        location = arrivalAirport.location,
                    ),
                    departureDate = it.departureDate,
                    arrivalDate = it.arrivalDate,
                    departureTime = it.departureTime,
                    arrivalTime = it.arrivalTime,
                    allTicketsCount = flightTickets.size,
                    businessTicketsCount = businessTicketsCount,
                    defaultTicketsCount = defaultTicketsCount,
                    businessPrice = businessPrice ?: 0,
                    defaultPrice = defaultPrice ?: 0,
                )
            )
        }

        return flightResponse
    }

    fun getFlightsById(flightId: String): FlightDTO{
        val flightsList: MutableList<FlightDTO> = mutableListOf()

        FlightsModel.select { FlightsModel.id eq flightId }.forEach {
            flightsList.add(
                FlightDTO(
                    id = it[FlightsModel.id],
                    departureAirportId = it[FlightsModel.departureAirportId],
                    arrivedAirportId = it[FlightsModel.arrivedAirportId],
                    departureTime = it[FlightsModel.departureTime],
                    arrivalTime = it[FlightsModel.arrivalTime],
                    arrivalDate = it[FlightsModel.arrivalDate],
                    departureDate = it[FlightsModel.departureDate],
                )
            )
        }

        return flightsList.first()
    }

    fun getMostAirportId(): String {
        val flightsList: MutableList<FlightDTO> = mutableListOf()

        FlightsModel.selectAll().forEach {
            flightsList.add(
                FlightDTO(
                    id = it[FlightsModel.id],
                    departureAirportId = it[FlightsModel.departureAirportId],
                    arrivedAirportId = it[FlightsModel.arrivedAirportId],
                    departureDate = it[FlightsModel.departureDate],
                    arrivalDate = it[FlightsModel.arrivalDate],
                    departureTime = it[FlightsModel.departureTime],
                    arrivalTime = it[FlightsModel.arrivalTime],
                )
            )
        }

        var idList: MutableList<String> = mutableListOf()

        flightsList.forEach {
            idList.add(it.arrivedAirportId)
        }

        idList = idList.distinct().toMutableList()

        val idCountList: MutableList<Int> = mutableListOf()

        idList.forEach {
            idCountList.add(
                flightsList.filter {flight ->  flight.arrivedAirportId == it}.size
            )
        }

        val indexMaxLocation = idCountList.indexOf(idCountList.max())

        return idList[indexMaxLocation]
    }

    fun getFlightsPerMonth(): Map<String, Int>{
        val flightsList: MutableList<FlightDTO> = mutableListOf()

        FlightsModel.selectAll().forEach {
            flightsList.add(
                FlightDTO(
                    id = it[FlightsModel.id],
                    departureAirportId = it[FlightsModel.departureAirportId],
                    arrivedAirportId = it[FlightsModel.arrivedAirportId],
                    departureDate = it[FlightsModel.departureDate],
                    arrivalDate = it[FlightsModel.arrivalDate],
                    departureTime = it[FlightsModel.departureTime],
                    arrivalTime = it[FlightsModel.arrivalTime],
                )
            )
        }

        val monthFlightCount: MutableList<String> = mutableListOf()

        flightsList.forEach {
            monthFlightCount.add(it.departureDate.substring(startIndex = 3, endIndex = 5))
        }

        return mutableMapOf(
            "Январь" to monthFlightCount.filter {month -> month == "01" }.size,
            "Февраль" to monthFlightCount.filter {month -> month == "02" }.size,
            "Март" to monthFlightCount.filter {month -> month == "03" }.size,
            "Апрель" to monthFlightCount.filter {month -> month == "04" }.size,
            "Май" to monthFlightCount.filter {month -> month == "05" }.size,
            "Июнь" to monthFlightCount.filter {month -> month == "06" }.size,
            "Июль" to monthFlightCount.filter {month -> month == "07" }.size,
            "Август" to monthFlightCount.filter {month -> month == "08" }.size,
            "Сентябрь" to monthFlightCount.filter {month -> month == "09" }.size,
            "Октябрь" to monthFlightCount.filter {month -> month == "10" }.size,
            "Ноябрь" to monthFlightCount.filter {month -> month == "11" }.size,
            "Декабрь" to monthFlightCount.filter {month -> month == "12" }.size,
        )
    }
}