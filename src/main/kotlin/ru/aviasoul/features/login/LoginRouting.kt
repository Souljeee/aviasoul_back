package ru.aviasoul.features.login

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.models.PassengersModel
import ru.aviasoul.features.login.models.LoginPayloadRemote
import ru.aviasoul.features.login.models.LoginResponseRemote
import ru.aviasoul.features.login.models.LogoutPayload
import java.util.*

fun Application.configureLoginRouting() {
    routing {
        post("/login") {
            val payload = call.receive<LoginPayloadRemote>()
            val currentUser = transaction {
                PassengersModel.fetchAllPassengers().firstOrNull { it.email == payload.email }
            }

            if (currentUser == null) {
                call.respond(LoginResponseRemote(token = null, message = "user not found"))
            } else {
                if (currentUser.password == payload.password) {
                    val token = UUID.randomUUID().toString()
                    PassengersModel.updateToken(id = currentUser.id, token = token)

                    call.respond(LoginResponseRemote(token = token, message = null))
                } else {
                    call.respond(LoginResponseRemote(token = null, message = "invalid password"))
                }
            }
        }
        post("/logout") {
            val payload = call.receive<LogoutPayload>()
            PassengersModel.resetToken(token = payload.token)
            call.respond(status = HttpStatusCode.OK, message = "Success")
        }
    }
}

