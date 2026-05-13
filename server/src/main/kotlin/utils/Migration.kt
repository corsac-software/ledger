package br.dev.corsac.ledger.utils

import br.dev.corsac.ledger.config.setupFlyway
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.Database
import org.postgresql.ds.PGSimpleDataSource

fun setupExposedMigrationDb(): Database? = withMigrationGenerationEnabled {
    val dataSource = PGSimpleDataSource().apply {
        setUrl("jdbc:postgresql://localhost:5433/migration")
        user = "sa"
        password = "sa"
    }

    setupFlyway(dataSource).migrate()

    return Database.connect(dataSource)
}

fun Application.resolveMigrationDb() = runBlocking { dependencies.resolve<Database>("exposedMigrationDb") }

fun isMigrationGenerationEnabled(): Boolean = System.getenv("GERAR_MIGRATIONS") == "1"

inline fun <T> withMigrationGenerationEnabled(block: () -> T): T? {
    return if (isMigrationGenerationEnabled()) {
        block()
    } else null
}