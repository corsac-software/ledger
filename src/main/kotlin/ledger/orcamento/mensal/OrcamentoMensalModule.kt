package br.dev.brunorsch.ledger.orcamento.mensal

import br.dev.brunorsch.ledger.orcamento.mensal.api.OrcamentosMensaisController
import br.dev.brunorsch.ledger.orcamento.mensal.data.gerarOrcamentoMensalMigrationScripts
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.service.OrcamentosMensaisService
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

    dependencies {
        provide { OrcamentosMensaisRepository() }
        provide { OrcamentosMensaisService(resolve()) }
        provide { OrcamentosMensaisController(resolve()) }
    }

    val controller: OrcamentosMensaisController by dependencies

    routing {
        route("/api/orcamentos-mensais") {
            get("/{id}") { controller.buscarPorId(call) }
            get("/{id}/lancamentos") { controller.buscarLancamentosPorId(call) }

            post { controller.criar(call) }

            delete("/{id}") { controller.excluir(call) }
        }
    }
}