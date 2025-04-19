package org.example

fun main() = kotlinx.coroutines.runBlocking {
    val pet = getPetById(1)
    println("Питомец: $pet")
}