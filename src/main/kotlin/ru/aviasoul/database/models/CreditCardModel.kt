package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.dto.CreditCardDTO
import java.util.UUID

object CreditCardModel: Table("credit_cards") {
    val id = varchar("id", 100)
    val number = varchar("number", 100)
    val date = varchar("date", 50)
    val cvv = varchar("cvv", 50)
    val passengerId = varchar("passenger_id", 100).references(PassengersModel.id)

    override val primaryKey = PrimaryKey(id)

    fun getCardsByPassengerId(passengerId: String): List<CreditCardDTO>{
        val cardsList: MutableList<CreditCardDTO> = mutableListOf()

        CreditCardModel.select {CreditCardModel.passengerId eq passengerId}.forEach {
            cardsList.add(
                CreditCardDTO(
                    id = it[CreditCardModel.id],
                    number = it[CreditCardModel.number],
                    date = it[CreditCardModel.date],
                    cvv = it[CreditCardModel.cvv],
                    passengerId = it[CreditCardModel.passengerId],
                )
            )
        }

        return cardsList
    }

    fun insertNewCard(
        number: String,
        date: String,
        cvv: String,
        passengerId: String,
    ) {
        transaction{
            val uuid = UUID.randomUUID().toString()

            CreditCardModel.insert {
                it[CreditCardModel.id] = uuid
                it[CreditCardModel.number] = number
                it[CreditCardModel.date] = date
                it[CreditCardModel.cvv] = cvv
                it[CreditCardModel.passengerId] = passengerId
            }
        }
    }

    fun deleteCard(cardId: String){
        transaction{
            CreditCardModel.deleteWhere {
                CreditCardModel.id eq cardId
            }
        }
    }

    fun getCardsCount(): Int{
        val cardsList: MutableList<CreditCardDTO> = mutableListOf()

        CreditCardModel.selectAll().forEach {
            cardsList.add(
                CreditCardDTO(
                    id = it[CreditCardModel.id],
                    number = it[CreditCardModel.number],
                    date = it[CreditCardModel.date],
                    cvv = it[CreditCardModel.cvv],
                    passengerId = it[CreditCardModel.passengerId],
                )
            )
        }

        return cardsList.size
    }
}