package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.statement.*
import io.ktor.http.*
import org.example.models.Pet

const val baseUrl = "https://petstore.swagger.io/v2/";
const val basePetUrl = "${baseUrl}pet";

// Общий HttpClient, который используется в тестах
val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        })
    }
}

suspend fun getPetById(id: Long): Pet {
    return client.get("$basePetUrl/$id").body()
}

suspend fun createPet(pet: Pet): Pet =
    client.post(basePetUrl) {
        contentType(ContentType.Application.Json)
        setBody(pet)
    }.body()

suspend fun updatePet(pet: Pet): Pet =
    client.put(basePetUrl) {
        contentType(ContentType.Application.Json)
        setBody(pet)
    }.body()

suspend fun deletePet(id: Long): HttpResponse =
    client.delete("$basePetUrl/$id")