package ru.aviasoul.database.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.aviasoul.database.dto.PassengerDTO
import java.time.LocalDateTime
import java.util.Date

object PassengersModel: Table("passengers") {
    val id = varchar("id", 100)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val dtBirth = varchar("dt_birth", 20)
    val email = text("email")
    val passportDetails = varchar("passport_details", 20)
    val password = varchar("password", 50)
    val token = varchar("token", 100).nullable()

    override val primaryKey = PrimaryKey(id)

    fun fetchAllPassengers(): List<PassengerDTO>{
        val passengersList: MutableList<PassengerDTO> = mutableListOf()

        PassengersModel.selectAll().forEach {
            passengersList.add(
                element = PassengerDTO(
                    id = it[id],
                    firstName = it[firstName],
                    lastName = it[lastName],
                    dtBirth = it[dtBirth],
                    email = it[email],
                    passportDetails = it[passportDetails],
                    password = it[password],
                )
            )
        }

        return passengersList
    }

    fun getPassengerByToken(token: String): PassengerDTO? {
        val passengersList: MutableList<PassengerDTO> = mutableListOf()

        PassengersModel.select{
            PassengersModel.token eq token
        }.forEach {
            passengersList.add(
                element = PassengerDTO(
                    id = it[id],
                    firstName = it[firstName],
                    lastName = it[lastName],
                    dtBirth = it[dtBirth],
                    email = it[email],
                    passportDetails = it[passportDetails],
                    password = it[password],
                )
            )
        }

        return if (passengersList.isEmpty()) null else passengersList[0]
    }

    fun insertPassenger(passengerDTO: PassengerDTO){
        transaction {
            PassengersModel.insert {
                it[id] = passengerDTO.id
                it[firstName] = passengerDTO.firstName
                it[lastName] = passengerDTO.lastName
                it[dtBirth] = passengerDTO.dtBirth
                it[email] = passengerDTO.email
                it[passportDetails] = passengerDTO.passportDetails
                it[password] = passengerDTO.password
            }
        }
    }

    fun updateToken(id: String, token: String?){
        transaction {
            PassengersModel.update ({PassengersModel.id eq id}){
                it[PassengersModel.token] = token
            }
        }
    }

    fun resetToken(token: String?){
        transaction {
            PassengersModel.update ({PassengersModel.token eq token}){
                it[PassengersModel.token] = null
            }
        }
    }

    fun updateProfileInfo(
        passengerId: String,
        firstName: String,
        lastName: String,
        dtBirth: String,
        email: String,
        passportDetails: String,
    ){
        transaction {
            PassengersModel.update ({PassengersModel.id eq passengerId}){
                it[PassengersModel.firstName] = firstName
                it[PassengersModel.lastName] = lastName
                it[PassengersModel.dtBirth] = dtBirth
                it[PassengersModel.email] = email
                it[PassengersModel.passportDetails] = passportDetails
            }
        }
    }

    fun getAverageAge() : Int {
        val passengers = fetchAllPassengers()

        val ageList: MutableList<Int> = mutableListOf()

        passengers.forEach {
            val currentDate = LocalDateTime.now().year
            val passengerDate = it.dtBirth.substring(startIndex = 6).toInt()

            val passengerAge = currentDate - passengerDate

            ageList.add(passengerAge)
        }

        return ageList.average().toInt()
    }
}