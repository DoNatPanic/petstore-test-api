package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(val id: Long? = null, val name: String? = null)