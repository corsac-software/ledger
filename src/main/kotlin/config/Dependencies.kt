package br.dev.brunorsch.config

import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager

fun Application.configureFrameworks() {
    dependencies {
        provide<HikariDataSource> { this@configureFrameworks.setupHikari(embedded = true)  }
        provide<Database> { setupPostgresDatabase(resolve()) }
        provide<Flyway> { setupFlyway(resolve(), firstTime = true) }
    }

    val database: Database by dependencies
    TransactionManager.defaultDatabase = database
}
