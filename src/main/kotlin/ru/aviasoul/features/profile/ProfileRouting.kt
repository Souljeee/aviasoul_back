package ru.aviasoul.features.profile

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.models.CreditCardModel
import ru.aviasoul.database.models.PassengersModel
import ru.aviasoul.database.models.TicketsModel
import ru.aviasoul.features.profile.models.*
import ru.aviasoul.features.tickets.models.BookTicketPayload

fun Application.configureProfileRouting(){
    routing {
        post("/get_user_by_token") {
            val payload = call.receive<GetUserByTokenPayload>()

            val currentUser = transaction {
                PassengersModel.getPassengerByToken(token = payload.token)
            }

            if(currentUser != null){
                call.respond(
                    PassengerResponse(
                        id = currentUser.id,
                        firstName = currentUser.firstName,
                        lastName = currentUser.lastName,
                        dtBirth = currentUser.dtBirth,
                        email = currentUser.email,
                        passportDetails = currentUser.passportDetails,
                    )
                )

                return@post
            }

            call.respond(status = HttpStatusCode.NoContent, message = "User is empty")
        }
        post("/add_credit_card") {
            val payload = call.receive<AddCreditCardPayload>()

            CreditCardModel.insertNewCard(
                number = payload.number,
                date = payload.date,
                cvv = payload.cvv,
                passengerId = payload.passengerId,
            )

            call.respond(status = HttpStatusCode.OK, message = "Success")
        }
        post("/get_credit_cards") {
            val payload = call.receive<CreditCardListPayload>()

            val credits = transaction {
                CreditCardModel.getCardsByPassengerId(
                    passengerId = payload.passengerId,
                )
            }

            val creditCardList: MutableList<CreditCardResponse> = mutableListOf()

            credits.forEach {
                creditCardList.add(
                    CreditCardResponse(
                        id = it.id,
                        number = it.number,
                        date = it.date,
                        cvv = it.cvv,
                    )
                )
            }

            call.respond(creditCardList)
        }
        post("/edit_profile_info") {
            val payload = call.receive<EditProfileInfoPayload>()

            transaction {
                PassengersModel.updateProfileInfo(
                    passengerId = payload.passengerId,
                    firstName = payload.firstName,
                    lastName = payload.lastName,
                    dtBirth = payload.dtBirth,
                    email = payload.email,
                    passportDetails = payload.passportDetails,
                )
            }

            call.respond(status = HttpStatusCode.OK, message = "Success")
        }
        post("/delete_card") {
            val payload = call.receive<DeleteCardPayload>()

            CreditCardModel.deleteCard(
                cardId = payload.cardId,
            )

            call.respond(status = HttpStatusCode.OK, message = "Success")
        }
    }
}