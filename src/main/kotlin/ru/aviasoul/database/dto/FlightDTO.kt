package ru.aviasoul.database.dto

class FlightDTO(
    val id: String,
    val departureAirportId: String,
    val arrivedAirportId: String,
    val departureTime: String,
    val arrivalTime: String,
    val arrivalDate: String,
    val departureDate: String,
)