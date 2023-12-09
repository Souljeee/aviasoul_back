package ru.aviasoul.features.registration

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.dto.PassengerDTO
import ru.aviasoul.database.models.PassengersModel
import ru.aviasoul.features.registration.models.RegisterResponse
import ru.aviasoul.features.registration.models.RegistrationPayload
import ru.aviasoul.plugins.Test
import java.util.*

fun Application.configureRegistrationRouting() {
    routing {
        post("/registration") {
            val payload = call.receive<RegistrationPayload>()

            val currentUser = transaction {
                PassengersModel.fetchAllPassengers().firstOrNull{ it.email == payload.email}
            }

            if(currentUser == null){
                val newUser = PassengerDTO(
                    id = UUID.randomUUID().toString(),
                    firstName = payload.firstName,
                    lastName = payload.lastName,
                    dtBirth = payload.dtBirth,
                    email = payload.email,
                    passportDetails = payload.passportDetails,
                    password = payload.password,
                )

                PassengersModel.insertPassenger(newUser)

                call.respond(RegisterResponse(accessAllowed = true))
            }else{
                call.respond(RegisterResponse(accessAllowed = false, message = "user already exist"))
            }
        }
    }
}