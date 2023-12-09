package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ru.aviasoul.database.dto.AirportDTO
import ru.aviasoul.database.dto.FlightDTO

object AirportsModel : Table("airports") {
    val id = varchar("id", 100)
    val name = varchar("name", 100)
    val location = varchar("location", 100)

    override val primaryKey = PrimaryKey(id)

    fun getAirportsByLocation(location: String): List<AirportDTO> {
        val airportsList: MutableList<AirportDTO> = mutableListOf()

        AirportsModel.select { AirportsModel.location eq location }.forEach {
            airportsList.add(
                AirportDTO(
                    id = it[AirportsModel.id],
                    name = it[AirportsModel.name],
                    location = it[AirportsModel.location],
                )
            )
        }

        return airportsList
    }

    fun getAirportById(id: String): AirportDTO {
        val airportsList: MutableList<AirportDTO> = mutableListOf()

        AirportsModel.select { AirportsModel.id eq id }.forEach {
            airportsList.add(
                AirportDTO(
                    id = it[AirportsModel.id],
                    name = it[AirportsModel.name],
                    location = it[AirportsModel.location],
                )
            )
        }

        return airportsList.first()
    }

    fun getMostAirportsLocation(): String {
        val airportsList: MutableList<AirportDTO> = mutableListOf()

        AirportsModel.selectAll().forEach {
            airportsList.add(
                AirportDTO(
                    id = it[AirportsModel.id],
                    name = it[AirportsModel.name],
                    location = it[AirportsModel.location],
                )
            )
        }

        var locationList: MutableList<String> = mutableListOf()

        airportsList.forEach {
            locationList.add(it.location)
        }

        locationList = locationList.distinct().toMutableList()

        val locationCountList: MutableList<Int> = mutableListOf()

        locationList.forEach {
            locationCountList.add(
                airportsList.filter {air ->  air.location == it}.size
            )
        }

        val indexMaxLocation = locationCountList.indexOf(locationCountList.max())

        return locationList[indexMaxLocation]
    }
}