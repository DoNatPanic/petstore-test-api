package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    val id: Long? = null,
    val category: Category? = null,
    val name: String? = null,
    val photoUrls: List<String> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val status: String? = null
)