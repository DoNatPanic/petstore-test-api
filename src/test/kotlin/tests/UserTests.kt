package org.example.tests

import kotlinx.coroutines.runBlocking
import org.example.TestConfig.client
import org.example.api.UserService
import org.example.models.User
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class UserTests {

    private val userService = UserService(client)

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

        val responseCreate = userService.createUser(newUser)
        assertTrue(
            responseCreate.status.value == 200,
            "Expected create user response status 200 but was ${responseCreate.status.value}"
        )
        if (responseCreate.status.value == 200) {
            println("User created: Username ${newUser.username}")
        }

        //  Получение по id
        val fetchedUser = userService.getUserByName(tempUserName)
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
        val updateResponse = userService.updateUser(changedUser)
        assertTrue(
            updateResponse.status.value == 200,
            "Expected update user response status 200 but was ${updateResponse.status.value}"
        )
        if (updateResponse.status.value == 200) {
            println("User updated: Username ${changedUser.username}")
        }

        // Вход в учетную запись
        val loginUserResponse = userService.loginUser(tempUserName, "1234")
        assertTrue(
            loginUserResponse.status.value == 200,
            "Expected login user response status 200 but was ${loginUserResponse.status.value}"
        )
        if (loginUserResponse.status.value == 200) {
            println("User logged in successfully")
        }

        // Выход из учетной записи
        val logoutUserResponse = userService.logoutUser()
        assertTrue(
            logoutUserResponse.status.value == 200,
            "Expected logout user response status 200 but was ${logoutUserResponse.status.value}"
        )
        if (logoutUserResponse.status.value == 200) {
            println("User logged out successfully")
        }

        // Удаление
        val deleteResponse = userService.deleteUser(tempUserName)
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
        val responseCreateList = userService.createUserList(list)
        assertTrue(
            responseCreateList.status.value == 200,
            "Expected create user list response status 200 but was ${responseCreateList.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("User list created: list size is ${list.size}")
        }

        // Создание списка типа Array пользователей
        val array = listOf(newUser)
        val responseCreateArray = userService.createUserList(list)
        assertTrue(
            responseCreateArray.status.value == 200,
            "Expected create user array response status 200 but was ${responseCreateArray.status.value}"
        )
        if (deleteResponse.status.value == 200) {
            println("User array created: array size is ${array.size}")
        }
    }

}