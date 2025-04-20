package org.example.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import org.example.models.PetStatus
import org.example.models.Response
import java.io.File
import org.example.models.Pet

const val basePetUrl = "https://petstore.swagger.io/v2/pet"

class PetService(private val client: HttpClient) {
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
}