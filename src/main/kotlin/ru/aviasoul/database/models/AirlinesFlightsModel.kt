package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.Table

object AirlinesFlightsModel : Table("airlines_flights") {
    val id = varchar("id", 100)
    val airlineId = varchar("airline_id", 100).references(AirlinesModel.id)
    val flightId = varchar("flight_id", 100).references(FlightsModel.id)

    override val primaryKey = PrimaryKey(id)
}