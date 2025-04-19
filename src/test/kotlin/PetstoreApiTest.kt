package org.example

import org.junit.jupiter.api.Test
import kotlinx.coroutines.runBlocking
import org.example.models.Category
import org.example.models.Order
import org.example.models.OrderStatus
import org.example.models.Pet
import org.example.models.PetStatus
import org.example.models.Tag
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PetstoreApiTest {

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

        val createdPet = createPet(newPet)
        println("POST status = ${createdPet.status}")
        assertEquals(tempPetId, createdPet.id)
        assertEquals(tempPetName, createdPet.name)
        println("Pet created: $createdPet")

        // Загрузка фото
        val fileUrl = javaClass.getResource("/dog.jpg") ?: throw IllegalArgumentException("File not found")
        val file = File(fileUrl.toURI())
        val result = uploadPetImage(tempPetId, file)
        println(result)

        // Получение по id
        val fetchedPet = getPetById(tempPetId)
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
        val updatedPet = updatePet(changedPet)
        println("PUT status = ${updatedPet.status}")
        assertEquals(tempPetName, updatedPet.name)
        assertEquals(updatedPetStatus.name, updatedPet.status)
        println("Pet updated: $updatedPet")

        // Обновление информации через форму
        val updatedByFormPet = updatePetFormData(petId = tempPetId, newName = "Rocky", newStatus = PetStatus.sold)
        assertTrue { updatedByFormPet.code == 200 }
        println("Update pet by form response message: ${updatedByFormPet.message}")

        // Удаление
        val deleteResponse = deletePet(tempPetId)
        println("DELETE status = ${deleteResponse.status}")
        assertEquals(200, deleteResponse.status.value)
        println("Pet deleted: ID $tempPetId")

        // Поиск по статусу
        val list = getByStatus(tempPetStatus)
        println("Pets with status '${tempPetStatus}': ${list.size}")
        assertTrue(list.isNotEmpty(), "The list of pets sold should not be empty.")
        assertTrue(list.all { it.status == tempPetStatus.name }, "All pets must have the status '${PetStatus.sold}'")
    }

    @Test
    fun `create, get, update and delete order`() = runBlocking {
        val tempOrderId = 999L // (1000000..1999999).random() // уникальный ID для изоляции
        val tempPetId = 999L
        val tempOrderStatus = OrderStatus.placed

        // Создание заказа
        val newOrder = Order(
            id = tempOrderId,
            petId = tempPetId,
            quantity = 0,
            shipDate = "2025-04-19T16:05:22.806Z",
            status = tempOrderStatus.name,
            complete = true
        )

        val createdOrder = createOrder(newOrder)
        println("POST status = ${createdOrder.status}")
        assertEquals(tempOrderId, createdOrder.id)
        assertEquals(tempPetId, createdOrder.petId)
        println("Pet created: $createdOrder")

        // Получение по id
        val fetchedOrder = getOrderById(tempOrderId)
        println("FETCH status = ${fetchedOrder.status}")
        assertEquals(tempOrderId, fetchedOrder.id)
        assertEquals(tempOrderStatus.name, fetchedOrder.status)
        println("Order fetched: $fetchedOrder")

        // Удаление
        val deleteResponse = deleteOrder(tempOrderId)
        println("DELETE status = ${deleteResponse.status}")
        assertEquals(200, deleteResponse.status.value)
        println("Order deleted: ID $tempPetId")

        val list = getInventories()
        println("Inventory size: ${list.size}")
        assertTrue(list.isNotEmpty(), "The list of inventories should not be empty.")
    }
}