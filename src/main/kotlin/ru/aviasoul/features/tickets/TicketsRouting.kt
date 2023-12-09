package ru.aviasoul.features.tickets

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.dto.TicketsDTO
import ru.aviasoul.database.models.AirportsModel
import ru.aviasoul.database.models.FlightsModel
import ru.aviasoul.database.models.PaymentsModel
import ru.aviasoul.database.models.TicketsModel
import ru.aviasoul.features.flights.models.FlightsPayload
import ru.aviasoul.features.tickets.models.*

fun Application.configureTicketsRouting(){
    routing {
        post("/book_ticket") {
            val payload = call.receive<BookTicketPayload>()

            val bookStatus = transaction {
               TicketsModel.bookTicket(
                   flightId = payload.flightId,
                   passengerId = payload.passengerId,
                   count = payload.count,
                   type = payload.type,
               )
            }

            if(bookStatus){
                call.respond(status = HttpStatusCode.OK, message = "Sucess")
            }
        }
        post ("/get_booked_tickets"){
            val payload = call.receive<BookedTicketPayload>()

            val bookedTickets = transaction {
                TicketsModel.getBookedTicketsByPassengerId(passengerId = payload.passengerId)
            }

            val response = createBookedResponse(bookedTickets)

            call.respond(response)
        }
        post ("/get_payed_tickets"){
            val payload = call.receive<BookedPayedPayload>()

            val payedTickets = transaction {
                TicketsModel.getPayedTicketsByPassengerId(passengerId = payload.passengerId)
            }

            val response = createPayedResponse(payedTickets)

            call.respond(response)
        }
        post ("/pay_for_ticket"){
            val payload = call.receive<PayForTicketsPayload>()

            val paymentId = PaymentsModel.makePayment(
                amount = payload.amount,
                transactionTime = payload.transactionTime
            )

            payload.ticketsId.forEach {
                TicketsModel.updatePaymentId(
                    ticketId = it,
                    paymentId = paymentId
                )
            }

            call.respond(status = HttpStatusCode.OK, message = "Success")
        }
    }
}

private fun createBookedResponse(bookedTickets: List<TicketsDTO>): List<BookedTicketsResponse>{
    var idList: MutableList<String> = mutableListOf()

    for (ticket in bookedTickets){
        idList.add(ticket.flightId)
    }

    idList = idList.distinct().toMutableList()

    val listResponse: MutableList<BookedTicketsResponse> = mutableListOf()

    idList.forEach {
        val currentFlightId  = it

        val currentFlight = transaction {
            FlightsModel.getFlightsById(currentFlightId)
        }

        val departureAirport = transaction {
            AirportsModel.getAirportById(currentFlight.departureAirportId)
        }

        val arrivalAirport = transaction {
            AirportsModel.getAirportById(currentFlight.arrivedAirportId)
        }

        val currentTickets = bookedTickets.filter {e -> e.flightId ==  currentFlightId}

        listResponse.add(
            BookedTicketsResponse(
                flightInfo = FlightResponse(
                    departureAirport = AirportResponse(
                        name = departureAirport.name,
                        location = departureAirport.location,
                    ),
                    arrivalAirport = AirportResponse(
                        name = arrivalAirport.name,
                        location = arrivalAirport.location,
                    ),
                    departureDate = currentFlight.departureDate,
                    arrivalDate = currentFlight.arrivalDate,
                    departureTime = currentFlight.departureTime,
                    arrivalTime = currentFlight.arrivalTime,
                ),
                tickets = currentTickets.map{ ticket ->
                    TicketResponse(
                        id = ticket.id,
                        seatNumber = ticket.seatNumber,
                        price = ticket.price,
                        type = ticket.type,
                    )
                }.toList()
            )
        )
    }

    return listResponse
}

private fun createPayedResponse(bookedTickets: List<TicketsDTO>): List<PayedTicketsResponse>{
    var idList: MutableList<String> = mutableListOf()

    for (ticket in bookedTickets){
        idList.add(ticket.flightId)
    }

    idList = idList.distinct().toMutableList()

    val listResponse: MutableList<PayedTicketsResponse> = mutableListOf()

    idList.forEach {
        val currentFlightId  = it

        val currentFlight = transaction {
            FlightsModel.getFlightsById(currentFlightId)
        }

        val departureAirport = transaction {
            AirportsModel.getAirportById(currentFlight.departureAirportId)
        }

        val arrivalAirport = transaction {
            AirportsModel.getAirportById(currentFlight.arrivedAirportId)
        }

        val currentTickets = bookedTickets.filter {e -> e.flightId ==  currentFlightId}

        listResponse.add(
            PayedTicketsResponse(
                flightInfo = FlightResponse(
                    departureAirport = AirportResponse(
                        name = departureAirport.name,
                        location = departureAirport.location,
                    ),
                    arrivalAirport = AirportResponse(
                        name = arrivalAirport.name,
                        location = arrivalAirport.location,
                    ),
                    departureDate = currentFlight.departureDate,
                    arrivalDate = currentFlight.arrivalDate,
                    departureTime = currentFlight.departureTime,
                    arrivalTime = currentFlight.arrivalTime,
                ),
                tickets = currentTickets.map{ ticket ->
                    TicketResponse(
                        id = ticket.id,
                        seatNumber = ticket.seatNumber,
                        price = ticket.price,
                        type = ticket.type,
                    )
                }.toList()
            )
        )
    }

    return listResponse
}