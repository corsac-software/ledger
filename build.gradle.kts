val exposed_version: String by project
val h2_version: String by project
val koin_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project
val flyway_version: String by project
val hikari_version: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.4.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
}

group = "br.dev.brunorsch"
version = "0.0.1"

application {
    mainClass = "br.dev.brunorsch.ApplicationKt"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("io.ktor:ktor-server-forwarded-header")
    implementation("io.ktor:ktor-server-di")
    implementation("io.ktor:ktor-server-hsts")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-routing-openapi")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-server-auth")
    implementation("com.kborowy:firebase-auth-provider:1.6.0")
    implementation("io.ktor:ktor-server-request-validation")
    implementation("io.ktor:ktor-server-host-common")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-migration-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")

    implementation("com.h2database:h2:$h2_version")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("org.flywaydb:flyway-core:$flyway_version")
    implementation("com.zaxxer:HikariCP:$hikari_version")

    implementation("io.ktor:ktor-server-netty")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
