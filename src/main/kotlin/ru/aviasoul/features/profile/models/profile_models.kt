package ru.aviasoul.features.profile.models

import kotlinx.serialization.Serializable

@Serializable
data class GetUserByTokenPayload(
    val token: String,
)

@Serializable
data class PassengerResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val dtBirth: String,
    val email: String,
    val passportDetails: String,
)

@Serializable
data class AddCreditCardPayload(
    val number: String,
    val date: String,
    val cvv: String,
    val passengerId: String,
)

@Serializable
data class CreditCardListPayload(
    val passengerId: String,
)

@Serializable
data class CreditCardResponse(
    val id: String,
    val number: String,
    val date: String,
    val cvv: String,
)

@Serializable
data class EditProfileInfoPayload(
    val passengerId: String,
    val firstName: String,
    val lastName: String,
    val dtBirth: String,
    val email: String,
    val passportDetails: String,
)

@Serializable
data class DeleteCardPayload(
    val cardId: String,
)