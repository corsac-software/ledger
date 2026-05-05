package br.dev.brunorsch.ledger.orcamento.mensal

import br.dev.brunorsch.ledger.utils.withMigrationGenerationEnabled
import br.dev.brunorsch.ledger.orcamento.mensal.api.*
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.CategoriasRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.gerarOrcamentoMensalMigrationScripts
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.routes.orcamentosMensaisRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.categoriasRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.service.CategoriasService
import br.dev.brunorsch.ledger.orcamento.mensal.service.OrcamentosMensaisService
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun Application.orcamentoMensalModule() {
    withMigrationGenerationEnabled {
        gerarOrcamentoMensalMigrationScripts()
    }

    dependencies {
        provide { OrcamentosMensaisRepository() }
        provide { OrcamentosMensaisService(resolve()) }
        provide { OrcamentosMensaisController(resolve()) }
        provide { CategoriasRepository() }
        provide { CategoriasService(resolve()) }
        provide { CategoriasController(resolve()) }
    }

    val controller: OrcamentosMensaisController by dependencies
    val categoriasController: CategoriasController by dependencies

    routing {
        orcamentosMensaisRoutes(controller)
        categoriasRoutes(categoriasController)
    }
}
