package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long? = null,
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val userStatus: Int? = null
)
