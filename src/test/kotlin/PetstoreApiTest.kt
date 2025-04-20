package org.example

import io.ktor.client.call.body
import org.junit.jupiter.api.Test
import kotlinx.coroutines.runBlocking
import org.example.models.Category
import org.example.models.Order
import org.example.models.OrderStatus
import org.example.models.Pet
import org.example.models.PetStatus
import org.example.models.Tag
import org.example.models.User
import java.io.File
import kotlin.test.assertEquals
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
        assertEquals(tempPetId, createdOrder.petId)
        assertTrue(
            createdOrder.status == newOrder.status,
            "Expected create order status ${newOrder.status} but was ${createdOrder.status}"
        )
        println("Order created: $createdOrder")

        // Получение по id
        val fetchedOrder = getOrderById(tempOrderId)
        assertTrue(
            fetchedOrder.status == newOrder.status,
            "Expected fetch order status ${newOrder.status} but was ${fetchedOrder.status}"
        )
        println("Order fetched: $fetchedOrder")

        // Удаление
        val deleteResponse = deleteOrder(tempOrderId)
        println("DELETE status = ${deleteResponse.status}")
        assertTrue(
            deleteResponse.status.value == 200,
            "Expected delete order response status 200 but was ${deleteResponse.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("Order deleted successfully")
        }

        val list = getInventories()
        println("Inventory size: ${list.size}")
        assertTrue(list.isNotEmpty(), "The list of inventories should not be empty.")
    }

    @Test
    fun `create, get, update and delete user`() = runBlocking {
        val tempUserId = 999L // (1000000..1999999).random() // уникальный ID для изоляции
        val tempUserName = "petr"
        val updatedFirstName = "Petr"
        val tempUserStatus = 0

        // Создание пользователя
        val newUser = User(
            id = tempUserId,
            username = tempUserName,
            firstName = "string",
            lastName = "string",
            email = "string",
            password = "string",
            phone = "string",
            userStatus = tempUserStatus
        )

        val responseCreate = createUser(newUser)
        assertTrue(
            responseCreate.status.value == 200,
            "Expected create user response status 200 but was ${responseCreate.status.value}"
        )
        if (responseCreate.status.value == 200) {
            println("User created: Username ${newUser.username}")
        }

        //  Получение по id
        val fetchedUser = getUserByName(tempUserName)
        assertTrue(
            tempUserName == fetchedUser.username,
            "Expected username $tempUserName but was ${fetchedUser.username}"
        )
        println("User fetched: $fetchedUser")

        // Изменение информации о пользователе
        val changedUser = User(
            id = tempUserId,
            username = tempUserName,
            firstName = updatedFirstName,
            lastName = "string",
            email = "string",
            password = "string",
            phone = "string",
            userStatus = tempUserStatus
        )

        // Обновление информации о пользователе
        val updateResponse = updateUser(changedUser)
        assertTrue(
            updateResponse.status.value == 200,
            "Expected update user response status 200 but was ${updateResponse.status.value}"
        )
        if (updateResponse.status.value == 200) {
            println("User updated: Username ${changedUser.username}")
        }

        // Вход в учетную запись
        val loginUserResponse = loginUser(tempUserName, "1234")
        assertTrue(
            loginUserResponse.status.value == 200,
            "Expected login user response status 200 but was ${loginUserResponse.status.value}"
        )
        if (loginUserResponse.status.value == 200) {
            println("User logged in successfully")
        }

        // Выход из учетной записи
        val logoutUserResponse = logoutUser()
        assertTrue(
            logoutUserResponse.status.value == 200,
            "Expected logout user response status 200 but was ${logoutUserResponse.status.value}"
        )
        if (logoutUserResponse.status.value == 200) {
            println("User logged out successfully")
        }

        // Удаление
        val deleteResponse = deleteUser(tempUserName)
        println("DELETE status = ${deleteResponse.status}")
        assertTrue(
            deleteResponse.status.value == 200,
            "Expected delete user response status 200 but was ${deleteResponse.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("User deleted: UserName $tempUserName")
        }

        // Создание списка типа List пользователей
        val list = listOf(newUser)
        val responseCreateList = createUserList(list)
        assertTrue(
            responseCreateList.status.value == 200,
            "Expected create user list response status 200 but was ${responseCreateList.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("User list created: list size is ${list.size}")
        }

        // Создание списка типа Array пользователей
        val array = listOf(newUser)
        val responseCreateArray = createUserList(list)
        assertTrue(
            responseCreateArray.status.value == 200,
            "Expected create user array response status 200 but was ${responseCreateArray.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("User array created: array size is ${array.size}")
        }
    }
}