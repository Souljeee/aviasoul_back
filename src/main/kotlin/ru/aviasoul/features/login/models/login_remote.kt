package ru.aviasoul.features.login.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginPayloadRemote(
    val email : String,
    val password: String,
)

@Serializable
data class LoginResponseRemote(
    val token: String?,
    val message: String? = null,
)

@Serializable
data class LogoutPayload(
    val token: String,
)
