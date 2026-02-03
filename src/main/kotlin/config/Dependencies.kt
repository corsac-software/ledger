package br.dev.brunorsch.config

import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureFrameworks() {
    dependencies {
        provide<HikariDataSource> { this@configureFrameworks.setupHikari(embedded = true)  }
        provide<Database> { setupPostgresDatabase(resolve()) }
        provide<Flyway> { setupFlyway(resolve(), firstTime = true) }
    }
}
