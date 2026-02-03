@file:OptIn(ExperimentalDatabaseMigrationApi::class)
package br.dev.brunorsch.orcamento.familiar.mensal.data

import br.dev.brunorsch.config.MIGRATIONS_DIR
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

fun gerarOrcamentoMensalMigrationScripts(database: Database) {
    transaction(database) {
        MigrationUtils.generateMigrationScript(
            OrcamentosMensaisTable, LancamentosMensaisTable,
            scriptDirectory = MIGRATIONS_DIR,
            scriptName = "V1__Add_tabelas_orcamentos_mensais",
        )
    }
}