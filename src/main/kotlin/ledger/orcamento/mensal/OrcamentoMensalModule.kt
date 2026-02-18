package br.dev.brunorsch.ledger.orcamento.mensal

import br.dev.brunorsch.ledger.orcamento.mensal.data.gerarOrcamentoMensalMigrationScripts
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun Application.orcamentoMensalModule() {
    val database: Database by dependencies
    val flyway: Flyway by dependencies

    gerarOrcamentoMensalMigrationScripts(database)

    transaction(database) {
        flyway.migrate()
    }

    routing {

    }
}