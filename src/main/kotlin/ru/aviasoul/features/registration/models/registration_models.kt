package ru.aviasoul.features.registration.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationPayload(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val dtBirth: String,
    val passportDetails: String,
)

@Serializable
data class RegisterResponse(
    val accessAllowed: Boolean,
    val message: String? = null,
)