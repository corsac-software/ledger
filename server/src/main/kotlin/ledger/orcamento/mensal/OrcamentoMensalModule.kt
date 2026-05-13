package br.dev.brunorsch.ledger.orcamento.mensal

import br.dev.brunorsch.ledger.orcamento.mensal.api.*
import br.dev.brunorsch.ledger.orcamento.mensal.data.gerarOrcamentoMensalMigrationScripts
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.*
import br.dev.brunorsch.ledger.orcamento.mensal.routes.cartoesRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.categoriasRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.lancamentosFixosRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.routes.orcamentosMensaisRoutes
import br.dev.brunorsch.ledger.orcamento.mensal.service.OrcamentosMensaisService
import br.dev.brunorsch.ledger.orcamento.mensal.service.cartoes.CartoesCrudService
import br.dev.brunorsch.ledger.orcamento.mensal.service.cartoes.FaturasCrudService
import br.dev.brunorsch.ledger.orcamento.mensal.service.cartoes.ParcelamentosCrudService
import br.dev.brunorsch.ledger.orcamento.mensal.service.lancamentos.CategoriasCrudService
import br.dev.brunorsch.ledger.orcamento.mensal.service.lancamentos.LancamentosFixosCrudService
import br.dev.brunorsch.ledger.orcamento.mensal.service.lancamentos.LancamentosMensaisService
import br.dev.brunorsch.ledger.utils.withMigrationGenerationEnabled
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
        provide { LancamentosMensaisRepository() }
        provide { LancamentosMensaisService(resolve(), resolve(), resolve()) }
        provide { OrcamentosMensaisService(resolve(), resolve()) }
        provide { OrcamentosMensaisController(resolve()) }
        provide { LancamentosMensaisController(resolve()) }
        provide { CategoriasRepository() }
        provide { CategoriasCrudService(resolve()) }
        provide { CategoriasController(resolve()) }
        provide { CartoesRepository() }
        provide { CartoesCrudService(resolve()) }
        provide { CartoesController(resolve()) }
        provide { ParcelamentosRepository() }
        provide { ParcelamentosCrudService(resolve()) }
        provide { ParcelamentosController(resolve()) }
        provide { LancamentosFixosRepository() }
        provide { LancamentosFixosCrudService(resolve()) }
        provide { LancamentosFixosController(resolve()) }
        provide { FaturasRepository() }
        provide { FaturasCrudService(resolve(), resolve(), resolve()) }
        provide { FaturasController(resolve()) }
    }

    val controller: OrcamentosMensaisController by dependencies
    val lancamentosMensaisController: LancamentosMensaisController by dependencies
    val categoriasController: CategoriasController by dependencies
    val cartoesController: CartoesController by dependencies
    val parcelamentosController: ParcelamentosController by dependencies
    val lancamentosFixosController: LancamentosFixosController by dependencies
    val faturasController: FaturasController by dependencies

    routing {
        orcamentosMensaisRoutes(controller, lancamentosMensaisController)
        categoriasRoutes(categoriasController)
        cartoesRoutes(cartoesController, parcelamentosController, faturasController)
        lancamentosFixosRoutes(lancamentosFixosController)
    }
}
