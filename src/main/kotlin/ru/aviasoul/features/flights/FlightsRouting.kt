package ru.aviasoul.features.flights

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.models.FlightsModel
import ru.aviasoul.features.flights.models.FlightsPayload

fun Application.configureFlightsRouting(){
    routing {
        post("/flights") {
            val payload = call.receive<FlightsPayload>()

            val flights = transaction {
                FlightsModel.getFlightsWithFilters(
                    departureCity = payload.departureCity,
                    arrivedCity = payload.arrivedCity,
                    departureDt = payload.departureDt,
                    arrivedDt = payload.arrivedDt,
                    minPrice = payload.minPrice,
                    maxPrice = payload.maxPrice,
                )
            }

            call.respond(flights)
        }
    }
}