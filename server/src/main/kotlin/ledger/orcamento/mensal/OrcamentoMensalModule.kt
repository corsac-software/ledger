package br.dev.brunorsch.ledger.orcamento.mensal

import br.dev.brunorsch.ledger.utils.withMigrationGenerationEnabled
import br.dev.brunorsch.ledger.orcamento.mensal.api.*
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.CartoesRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.CategoriasRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.gerarOrcamentoMensalMigrationScripts
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.LancamentosFixosRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.ParcelamentosRepository
import br.dev.brunorsch.ledger.orcamento.mensal.routes.cartoesRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.lancamentosFixosRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.orcamentosMensaisRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.categoriasRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.service.CartoesService
import br.dev.brunorsch.ledger.orcamento.mensal.service.CategoriasService
import br.dev.brunorsch.ledger.orcamento.mensal.service.LancamentosFixosService
import br.dev.brunorsch.ledger.orcamento.mensal.service.OrcamentosMensaisService
import br.dev.brunorsch.ledger.orcamento.mensal.service.ParcelamentosService
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
        provide { CartoesRepository() }
        provide { CartoesService(resolve()) }
        provide { CartoesController(resolve()) }
        provide { ParcelamentosRepository() }
        provide { ParcelamentosService(resolve()) }
        provide { ParcelamentosController(resolve()) }
        provide { LancamentosFixosRepository() }
        provide { LancamentosFixosService(resolve()) }
        provide { LancamentosFixosController(resolve()) }
    }

    val controller: OrcamentosMensaisController by dependencies
    val categoriasController: CategoriasController by dependencies
    val cartoesController: CartoesController by dependencies
    val parcelamentosController: ParcelamentosController by dependencies
    val lancamentosFixosController: LancamentosFixosController by dependencies

    routing {
        orcamentosMensaisRoutes(controller)
        categoriasRoutes(categoriasController)
        cartoesRoutes(cartoesController, parcelamentosController)
        lancamentosFixosRoutes(lancamentosFixosController)
    }
}
