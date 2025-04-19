plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("test"))

    // Ktor Client
    val ktor = "3.1.2"
    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")

    // Kotlinx Serialization
    val version = "1.8.0"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$version")

    // JUnit 5
    val junit = "5.12.2"
    testImplementation("org.junit.jupiter:junit-jupiter:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Logback
    val logger = "1.5.4"
    implementation("ch.qos.logback:logback-classic:$logger")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}