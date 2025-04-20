package org.example.api

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.example.models.User

const val baseUserUrl = "https://petstore.swagger.io/v2/user"

class UserService(private val client: HttpClient) {

    // POST creates list of users with given input array
    suspend fun createUserList(list: List<User>): HttpResponse =
        client.post("${baseUserUrl}/createWithList") {
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
}