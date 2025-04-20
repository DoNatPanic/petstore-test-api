package org.example.tests

import kotlinx.coroutines.runBlocking
import org.example.TestConfig.client
import org.example.api.OrderService
import org.example.models.Order
import org.example.models.OrderStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderTests {

    private val orderService = OrderService(client)

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

        val createdOrder = orderService.createOrder(newOrder)
        println("POST status = ${createdOrder.status}")
        assertEquals(tempPetId, createdOrder.petId)
        assertTrue(
            createdOrder.status == newOrder.status,
            "Expected create order status ${newOrder.status} but was ${createdOrder.status}"
        )
        println("Order created: $createdOrder")

        // Получение по id
        val fetchedOrder = orderService.getOrderById(tempOrderId)
        assertTrue(
            fetchedOrder.status == newOrder.status,
            "Expected fetch order status ${newOrder.status} but was ${fetchedOrder.status}"
        )
        println("Order fetched: $fetchedOrder")

        // Удаление
        val deleteResponse = orderService.deleteOrder(tempOrderId)
        println("DELETE status = ${deleteResponse.status}")
        assertTrue(
            deleteResponse.status.value == 200,
            "Expected delete order response status 200 but was ${deleteResponse.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("Order deleted successfully")
        }

        val list = orderService.getInventories()
        println("Inventory size: ${list.size}")
        assertTrue(list.isNotEmpty(), "The list of inventories should not be empty.")
    }
}