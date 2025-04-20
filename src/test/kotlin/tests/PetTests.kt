package org.example.tests

import kotlinx.coroutines.runBlocking
import org.example.TestConfig.client
import org.example.api.PetService
import org.example.models.Category
import org.example.models.Pet
import org.example.models.PetStatus
import org.example.models.Tag
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PetTests {

    private val petService = PetService(client)

    @Test
    fun `create, get, update and delete pet`() = runBlocking {
        val tempPetId = 999L // (1000000..1999999).random() // уникальный ID для изоляции
        val tempPetName = "GoodBoy"
        val tempPetStatus = PetStatus.available
        val updatedPetStatus = PetStatus.sold

        // Создание питомца
        val newPet = Pet(
            id = tempPetId,
            name = tempPetName,
            category = Category(1L, "dogs"),
            photoUrls = listOf("https://example.com/somedog.jpg"),
            tags = listOf(Tag(1, "test")),
            status = tempPetStatus.name
        )

        val createdPet = petService.createPet(newPet)
        println("POST status = ${createdPet.status}")
        assertEquals(tempPetName, createdPet.name)
        println("Pet created: $createdPet")

        // Загрузка фото
        val fileUrl = javaClass.getResource("/dog.jpg") ?: throw IllegalArgumentException("File not found")
        val file = File(fileUrl.toURI())
        val result = petService.uploadPetImage(tempPetId, file)
        println(result)

        // Получение по id
        val fetchedPet = petService.getPetById(tempPetId)
        println("FETCH status = ${fetchedPet.status}")
        assertEquals(tempPetName, fetchedPet.name)
        assertEquals(tempPetStatus.name, fetchedPet.status)
        println("Pet fetched: $fetchedPet")

        // Изменение информации о питомце
        val changedPet = Pet(
            id = tempPetId,
            name = tempPetName,
            category = Category(1L, "dogs"),
            photoUrls = listOf("https://example.com/updated.jpg"),
            tags = listOf(Tag(1L, "updated")),
            status = updatedPetStatus.name
        )

        // Обновление информации о питомце
        val updatedPet = petService.updatePet(changedPet)
        println("PUT status = ${updatedPet.status}")
        assertEquals(tempPetName, updatedPet.name)
        assertEquals(updatedPetStatus.name, updatedPet.status)
        println("Pet updated: $updatedPet")

        // Обновление информации через форму
        val updatedByFormPet =
            petService.updatePetFormData(petId = tempPetId, newName = "Rocky", newStatus = PetStatus.sold)
        assertTrue { updatedByFormPet.code == 200 }
        println("Update pet by form response message: ${updatedByFormPet.message}")

        // Удаление
        val deleteResponse = petService.deletePet(tempPetId)
        println("DELETE status = ${deleteResponse.status}")
        assertEquals(200, deleteResponse.status.value)
        println("Pet deleted: ID $tempPetId")

        // Поиск по статусу
        val list = petService.getByStatus(tempPetStatus)
        println("Pets with status '${tempPetStatus}': ${list.size}")
        assertTrue(list.isNotEmpty(), "The list of pets sold should not be empty.")
        assertTrue(list.all { it.status == tempPetStatus.name }, "All pets must have the status '${PetStatus.sold}'")
    }
}