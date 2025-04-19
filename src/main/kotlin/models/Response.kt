package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Response(val code: Int? = null, val type: String? = null, val message: String? = null)
