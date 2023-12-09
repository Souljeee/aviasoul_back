package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.aviasoul.database.dto.FlightDTO
import ru.aviasoul.database.dto.TicketsDTO

object TicketsModel: Table("tickets") {
    val id = varchar("id", 100)
    val seatNumber = varchar("seat_number", 10)
    val price = integer("price")
    val flightId = varchar("flight_id", 100).references(FlightsModel.id)
    val type = varchar("type", 20)
    val passengerId = varchar("passsenger_id", 100).references(PassengersModel.id).nullable()
    val paymentId = varchar("payment_id", 100).references(PaymentsModel.id).nullable()

    override val primaryKey = PrimaryKey(id)

    fun getTicketsByFlightId(
        flightId: String,
        minPrice: Int? = null,
        maxPrice: Int? = null,
    ): List<TicketsDTO>{
        var ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select { TicketsModel.flightId eq flightId  and(TicketsModel.passengerId eq null)}.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        if(minPrice != null){
            ticketsList = ticketsList.filter { it.price >= minPrice }.toMutableList()
        }

        if(maxPrice != null){
            ticketsList = ticketsList.filter { it.price >= maxPrice }.toMutableList()
        }

        return ticketsList
    }

    fun bookTicket(
        flightId: String,
        count: Int,
        type: String,
        passengerId: String,
    ): Boolean {
        try{
            val ticketsList: MutableList<TicketsDTO> = mutableListOf()

            TicketsModel.select {
                TicketsModel.flightId eq flightId and(TicketsModel.type eq type) and(TicketsModel.passengerId eq null)
            }.forEach {
                ticketsList.add(
                    TicketsDTO(
                        id = it[TicketsModel.id],
                        seatNumber = it[TicketsModel.seatNumber],
                        price = it[TicketsModel.price],
                        flightId = it[TicketsModel.flightId],
                        passengerId = it[TicketsModel.passengerId],
                        paymentId = it[TicketsModel.paymentId],
                        type = it[TicketsModel.type],
                    )
                )
            }

            if(ticketsList.size < count){
                return false
            }

            for(i in 0 until count){
                transaction {
                    TicketsModel.update({
                        TicketsModel.id eq ticketsList[i].id
                    }) {
                        it[TicketsModel.passengerId] = passengerId
                    }
                }
            }

            return true
        }catch (e: Exception){
            return false
        }
    }

    fun getBookedTicketsByPassengerId(passengerId: String): List<TicketsDTO>{
        val ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select {
            TicketsModel.passengerId eq passengerId and(TicketsModel.paymentId eq null)
        }.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        return ticketsList
    }

    fun getPayedTicketsByPassengerId(passengerId: String): List<TicketsDTO>{
        val ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select {
            TicketsModel.passengerId eq passengerId and(TicketsModel.paymentId neq null)
        }.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        return ticketsList
    }

    fun updatePaymentId(
        ticketId: String,
        paymentId: String
    ){
        transaction {
            TicketsModel.update({
                TicketsModel.id eq ticketId
            }) {
                it[TicketsModel.paymentId] = paymentId
            }
        }
    }

    fun getMaxTicketPrice(): Int{
        val ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select { TicketsModel.flightId eq flightId  and(TicketsModel.passengerId eq null)}.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        val prices: MutableList<Int> = mutableListOf()

        ticketsList.forEach {
            prices.add(it.price)
        }

        return prices.max()
    }

    fun getMinTicketPrice(): Int{
        val ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select { TicketsModel.flightId eq flightId  and(TicketsModel.passengerId eq null)}.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        val prices: MutableList<Int> = mutableListOf()

        ticketsList.forEach {
            prices.add(it.price)
        }

        return prices.min()
    }

    fun getBusinessTicketsCount(): Int{
        val ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select { TicketsModel.flightId eq flightId  and(TicketsModel.passengerId eq null)}.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        return ticketsList.filter { it.type == "business" }.size
    }

    fun getDefaultTicketsCount(): Int{
        val ticketsList: MutableList<TicketsDTO> = mutableListOf()

        TicketsModel.select { TicketsModel.flightId eq flightId  and(TicketsModel.passengerId eq null)}.forEach {
            ticketsList.add(
                TicketsDTO(
                    id = it[TicketsModel.id],
                    seatNumber = it[TicketsModel.seatNumber],
                    price = it[TicketsModel.price],
                    flightId = it[TicketsModel.flightId],
                    passengerId = it[TicketsModel.passengerId],
                    paymentId = it[TicketsModel.paymentId],
                    type = it[TicketsModel.type],
                )
            )
        }

        return ticketsList.filter { it.type == "default" }.size
    }
}