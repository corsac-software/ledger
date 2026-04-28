package br.dev.brunorsch.config

import br.dev.brunorsch.ledger.utils.resolveDirectoryFromRoot
import br.dev.brunorsch.ledger.utils.setupExposedMigrationDb
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.di.dependencies
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

fun Application.configureFrameworks() {
    dependencies {
        provide<HikariDataSource> { this@configureFrameworks.setupHikari(embedded = true) }
        provide<Database?>("exposedMigrationDb") { setupExposedMigrationDb() }
        provide<Database> { setupPostgresDatabase(resolve()) }
        provide<Flyway> { setupFlyway(resolve()) }
        provide<HoconApplicationConfig> { HoconApplicationConfig(ConfigFactory.load()) }
        provide<DatabaseConfig> {
            val config = resolve<HoconApplicationConfig>()
            DatabaseConfig(
                h2DbPath = config.property("h2.dbPath").getString(),
            )
        }
        provide<SecurityConfig> {
            val config = resolve<HoconApplicationConfig>()
            SecurityConfig(
                adminFile = config.property("firebase.adminFile").getString(),
            )
        }
    }

    configureGeneratedMigrationsDir()

    val flyway: Flyway by dependencies
    flyway.migrate()

    val database: Database by dependencies
    TransactionManager.defaultDatabase = database
}

fun Application.configureGeneratedMigrationsDir() {
    log.info("Criando pasta de migrations geradas...")
    resolveDirectoryFromRoot(GENERATED_MIGRATIONS_DIR).also {
        val result = it.mkdirs()
        if (result) {
            log.info("Pasta de migrations geradas criada com sucesso.")
        } else {
            log.info("Pasta de migrations geradas já existe ou não pôde ser criada.")
        }
    }
}
