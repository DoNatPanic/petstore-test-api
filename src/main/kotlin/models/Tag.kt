package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Tag(val id: Int? = null, val name: String? = null)
