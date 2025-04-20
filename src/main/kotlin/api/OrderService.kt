package org.example.api

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.example.models.Order

const val baseOrderUrl = "https://petstore.swagger.io/v2/store"

class OrderService(private val client: HttpClient) {
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
}

