package ru.aviasoul

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database
import ru.aviasoul.features.flights.configureFlightsRouting
import ru.aviasoul.features.login.configureLoginRouting
import ru.aviasoul.features.profile.configureProfileRouting
import ru.aviasoul.features.registration.configureRegistrationRouting
import ru.aviasoul.features.statistics.configureStatisticsRouting
import ru.aviasoul.features.tickets.configureTicketsRouting
import ru.aviasoul.plugins.*

fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/aviasoul",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "1q2w3e4r5t",
    )
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureRouting()
    configureSerialization()
    configureRegistrationRouting()
    configureLoginRouting()
    configureFlightsRouting()
    configureTicketsRouting()
    configureProfileRouting()
    configureStatisticsRouting()
}
