package ru.aviasoul.features.statistics

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.models.TicketsModel

fun Application.configureStatisticsRouting(){
    routing {
        post("/statistics") {
            val statistics = StatisticsController.getStatistics()

            call.respond(statistics)
        }
    }
}