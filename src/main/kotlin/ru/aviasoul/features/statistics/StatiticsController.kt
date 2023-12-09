package ru.aviasoul.features.statistics

import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.models.*

object StatisticsController {
    fun getStatistics() : StatisticsResponse {
        val averageAge = transaction{
            PassengersModel.getAverageAge()
        }

        val mostAirports = transaction {
            AirportsModel.getMostAirportsLocation()
        }

        val cardsCount = transaction {
            CreditCardModel.getCardsCount()
        }

        val mostArrivedCity = transaction {
            val airportId = FlightsModel.getMostAirportId()

            AirportsModel.getAirportById(airportId).location
        }

        val flightsPerMonth = transaction {
            FlightsModel.getFlightsPerMonth()
        }

        val maxPrice = transaction {
            TicketsModel.getMaxTicketPrice()
        }

        val minPrice = transaction {
            TicketsModel.getMinTicketPrice()
        }

        val businessCount = transaction {
            TicketsModel.getBusinessTicketsCount()
        }

        val defaultCount = transaction {
            TicketsModel.getDefaultTicketsCount()
        }

        val paymentsCount = transaction {
            PaymentsModel.getPaymentsCount()
        }

        return StatisticsResponse(
            averageAge = averageAge,
            mostAirportsLocation = mostAirports,
            cardsCount = cardsCount,
            mostArrivedCity = mostArrivedCity,
            flightsPerMonth = flightsPerMonth,
            maxPrice = maxPrice,
            minPrice = minPrice,
            businessCount = businessCount,
            paymentsCount = paymentsCount,
            defaultCount = defaultCount,
        )
    }
}