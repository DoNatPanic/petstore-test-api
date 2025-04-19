package org.example

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import org.junit.jupiter.api.Test
import kotlinx.coroutines.runBlocking
import org.example.models.Category
import org.example.models.Pet
import org.example.models.Tag
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PetstoreApiTest {

    @Test
    fun `create, get, update and delete pet`() = runBlocking {
        val tempId = 999 // (1000000..1999999).random() // уникальный ID для изоляции
        val tempName = "GoodBoy"

        // 1. Создание питомца
        val newPet = Pet(
            id = tempId,
            name = tempName,
            category = Category(1, "dogs"),
            photoUrls = listOf("https://example.com/somedog.jpg"),
            tags = listOf(Tag(1, "test")),
            status = "available"
        )

        val createdPet = createPet(newPet)
        println("GET status = ${createdPet.status}")
        assertEquals(tempId, createdPet.id)
        assertNotNull(createdPet.name)
        assertEquals(tempName, createdPet.name)
        println("Pet created: $createdPet")

        // 2. Получение по ID
        val fetchedPet = getPetById(tempId)
        println("FETCH status = ${fetchedPet.status}")
        assertEquals(tempName, fetchedPet.name)
        assertEquals("available", fetchedPet.status)
        println("Pet fetched: $fetchedPet")

        // 3. Изменение информации о питомце
        val changedPet = Pet(
            id = tempId,
            name = tempName,
            category = Category(1, "dogs"),
            photoUrls = listOf("https://example.com/updated.jpg"),
            tags = listOf(Tag(1, "updated")),
            status = "sold"
        )

        // 4. Обновление
        val updatedPet = updatePet(changedPet)
        println("PUT status = ${updatedPet.status}")
        assertEquals(tempName, updatedPet.name)
        assertEquals("sold", updatedPet.status)
        println("Pet updated: $updatedPet")

        // 5. Удаление
        val deleteResponse = deletePet(tempId)
        println("DELETE status = ${deleteResponse.status}")
        assertEquals(200, deleteResponse.status.value)
        println("Pet deleted: ID $tempId")
    }
}