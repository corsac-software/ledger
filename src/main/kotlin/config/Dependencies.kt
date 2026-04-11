package br.dev.brunorsch.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.config.property
import io.ktor.server.plugins.di.dependencies
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

fun Application.configureFrameworks() {
    dependencies {
        provide<HikariDataSource> { this@configureFrameworks.setupHikari(embedded = true)  }
        provide<Database> { setupPostgresDatabase(resolve()) }
        provide<Flyway> { setupFlyway(resolve(), firstTime = true) }
        provide<HoconApplicationConfig> { HoconApplicationConfig(ConfigFactory.load()) }
        provide<SecurityConfig> {
            val config = resolve<HoconApplicationConfig>()
            SecurityConfig(
                adminFile = config.property("firebase.adminFile").getString(),
            )
        }
    }

    val database: Database by dependencies
    TransactionManager.defaultDatabase = database
}
