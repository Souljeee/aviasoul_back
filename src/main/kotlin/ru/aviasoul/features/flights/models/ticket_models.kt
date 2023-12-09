package ru.aviasoul.features.flights.models

import kotlinx.serialization.Serializable

@Serializable
data class FlightsPayload(
    val departureCity: String?,
    val arrivedCity: String?,
    val departureDt: String?,
    val arrivedDt: String?,
    val minPrice: Int?,
    val maxPrice: Int?,
)

@Serializable
data class FlightResponse(
    val id: String,
    val departureAirport: AirportResponse,
    val arrivalAirport: AirportResponse,
    val departureDate: String,
    val arrivalDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val allTicketsCount: Int,
    val defaultTicketsCount: Int,
    val businessTicketsCount: Int,
    val businessPrice: Int,
    val defaultPrice: Int,
)

@Serializable
data class AirportResponse(
    val name: String,
    val location: String,
)