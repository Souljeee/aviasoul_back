package ru.aviasoul.features.statistics

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponse(
    val averageAge: Int,
    val mostAirportsLocation: String,
    val cardsCount: Int,
    val mostArrivedCity: String,
    val maxPrice: Int,
    val minPrice: Int,
    val businessCount: Int,
    val defaultCount: Int,
    val paymentsCount: Int,
    val flightsPerMonth: Map<String, Int>
)