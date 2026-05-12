@file:OptIn(ExperimentalDatabaseMigrationApi::class)

package br.dev.brunorsch.ledger.orcamento.mensal.data

import br.dev.brunorsch.config.GENERATED_MIGRATIONS_DIR
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.*
import br.dev.brunorsch.ledger.utils.resolveMigrationDb
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

fun Application.gerarOrcamentoMensalMigrationScripts() {
    val migrationDb = resolveMigrationDb()

    transaction(migrationDb) {
        MigrationUtils.generateMigrationScript(
            OrcamentosMensaisTable, LancamentosMensaisTable, CategoriasTable, CartoesTable, ParcelamentosTable,
            LancamentosFixosTable, FaturasTable,
            scriptDirectory = GENERATED_MIGRATIONS_DIR,
            scriptName = "migration_orcamentos_mensais",
        )
    }
}
