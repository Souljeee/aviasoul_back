package ru.aviasoul.database.dto

class TicketsDTO(
    val id: String,
    val seatNumber: String,
    val price: Int,
    val type: String,
    val flightId: String,
    val passengerId: String?,
    val paymentId: String?,
)