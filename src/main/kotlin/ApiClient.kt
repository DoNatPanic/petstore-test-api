package org.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.formData
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.statement.*
import io.ktor.http.*
import org.example.models.Order
import org.example.models.Pet
import org.example.models.Response
import io.ktor.client.request.forms.*
import org.example.models.PetStatus
import org.example.models.User
import java.io.File


const val baseUrl = "https://petstore.swagger.io/v2/"
const val basePetUrl = "${baseUrl}pet"
const val baseOrderUrl = "${baseUrl}store"
const val baseUserUrl = "${baseUrl}user"

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

// ***** PET *****

// POST uploads an image
suspend fun uploadPetImage(petId: Long, imageFile: File): Response {
    val response = client.post("https://petstore.swagger.io/v2/pet/$petId/uploadImage") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("additionalMetadata", "Cute image upload!")
                    append("file", imageFile.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                    })
                }
            )
        )
    }
    return response.body()
}

// POST add a new pet to the store
suspend fun createPet(pet: Pet): Pet =
    client.post(basePetUrl) {
        contentType(ContentType.Application.Json)
        setBody(pet)
    }.body()

// PUT update an existing pet
suspend fun updatePet(pet: Pet): Pet =
    client.put(basePetUrl) {
        contentType(ContentType.Application.Json)
        setBody(pet)
    }.body()

// GET finds pets by status
suspend fun getByStatus(status: PetStatus): List<Pet> {
    return client.get("$basePetUrl/findByStatus") {
        parameter("status", status)
        accept(ContentType.Application.Json)
    }.body()
}

// GET find pet by id
suspend fun getPetById(id: Long): Pet {
    return client.get("$basePetUrl/$id").body()
}

// POST updates a pet in the store with form data
suspend fun updatePetFormData(petId: Long, newName: String, newStatus: PetStatus): Response {
    val response = client.submitForm(
        url = "https://petstore.swagger.io/v2/pet/$petId",
        formParameters = Parameters.build {
            append("name", newName)
            append("status", newStatus.name)
        }
    )
    return response.body()
}

// DELETE deletes a pet
suspend fun deletePet(id: Long): HttpResponse =
    client.delete("$basePetUrl/$id")


// ***** ORDER *****

// GET returns pet inventories by status
suspend fun getInventories(): Map<String, Int> {
    return client.get("$baseOrderUrl/inventory").body()
}

// POST place an order for a pet
suspend fun createOrder(order: Order): Order =
    client.post("$baseOrderUrl/order") {
        contentType(ContentType.Application.Json)
        setBody(order)
    }.body()

// GET find purchase order by ID
suspend fun getOrderById(id: Long): Order {
    return client.get("$baseOrderUrl/order/$id").body()
}

// DELETE purchase order by ID
suspend fun deleteOrder(id: Long): HttpResponse =
    client.delete("$baseOrderUrl/order/$id")


// ***** USER *****

// POST creates list of users with given input array
suspend fun createUserList(list: List<User>): HttpResponse =
    client.post("$baseUserUrl/createWithList") {
        contentType(ContentType.Application.Json)
        setBody(list)
    }.body()


// GET get user by username
suspend fun getUserByName(userName: String): User {
    return client.get("$baseUserUrl/$userName").body()
}

// PUT updated user
suspend fun updateUser(user: User): HttpResponse =
    client.put("$baseUserUrl/${user.username}") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.body()

// DELETE delete user
suspend fun deleteUser(userName: String): HttpResponse =
    client.delete("$baseUserUrl/$userName")

// GET logs user into the system
suspend fun loginUser(userName: String, password: String): HttpResponse =
    client.get("$baseUserUrl/login") {
        parameter("username", userName)
        parameter("password", password)
    }

// GET logs out current logged in user session
suspend fun logoutUser(): HttpResponse =
    client.get("$baseUserUrl/logout")

// POST creates list of users with given input array
suspend fun createUserArray(array: Array<User>): HttpResponse =
    client.post("$baseUserUrl/createWithList") {
        contentType(ContentType.Application.Json)
        setBody(array)
    }.body()

// POST create user
suspend fun createUser(user: User): HttpResponse {
    val response = client.post(baseUserUrl) {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
    return response
}