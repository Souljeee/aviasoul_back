package ru.aviasoul.features.tickets.models

import kotlinx.serialization.Serializable

@Serializable
data class BookTicketPayload(
    val flightId: String,
    val passengerId: String,
    val count: Int,
    val type: String,
)

@Serializable
data class BookedTicketPayload(
    val passengerId: String,
)

@Serializable
data class BookedPayedPayload(
    val passengerId: String,
)

@Serializable
data class BookedTicketsResponse(
    val flightInfo: FlightResponse,
    val tickets: List<TicketResponse>,
)

@Serializable
data class PayedTicketsResponse(
    val flightInfo: FlightResponse,
    val tickets: List<TicketResponse>,
)

@Serializable
data class TicketResponse(
    val id: String,
    val seatNumber: String,
    val price: Int,
    val type: String,
)

@Serializable
data class FlightResponse(
    val departureAirport: AirportResponse,
    val arrivalAirport: AirportResponse,
    val departureDate: String,
    val arrivalDate: String,
    val departureTime: String,
    val arrivalTime: String,
)
@Serializable
data class AirportResponse(
    val name: String,
    val location: String,
)

@Serializable
data class PayForTicketsPayload(
    val ticketsId: List<String>,
    val amount: Int,
    val transactionTime: String,
)
