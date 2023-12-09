package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.dto.PaymentDTO
import java.util.*

object PaymentsModel : Table("payments") {
    val id = varchar("id", 100)
    val amount = integer("amount")
    val transactionDt = varchar("transaction_datetime", 100).nullable()

    override val primaryKey = PrimaryKey(id)

    fun makePayment(
        amount: Int,
        transactionTime: String
    ): String {
        val payId = transaction{
            val uuid = UUID.randomUUID().toString()

            PaymentsModel.insert {
                it[PaymentsModel.id] = uuid
                it[PaymentsModel.amount] = amount
                it[PaymentsModel.transactionDt] = transactionTime
            }

            return@transaction uuid
        }

        return payId
    }

    fun getPaymentsCount(): Int{
        val paymentsList: MutableList<PaymentDTO> = mutableListOf()

        PaymentsModel.selectAll().forEach {
            paymentsList.add(
                PaymentDTO(
                    id = it[PaymentsModel.id],
                    amount = it[PaymentsModel.amount],
                    transactionDt = it[PaymentsModel.transactionDt]!!,
                )
            )
        }

        return paymentsList.size
    }
}