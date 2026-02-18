package br.dev.brunorsch.ledger.orcamento.mensal.data.repository

import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.LancamentosMensaisTable
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.OrcamentosMensaisTable
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toLancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toOrcamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import org.jetbrains.exposed.v1.core.JoinType.INNER
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class OrcamentosMensaisRepository {
    fun criar(orcamentoMensal: OrcamentoMensal): OrcamentoMensal = transaction {
        val id = OrcamentosMensaisTable.insertAndGetId { orcamentoMensal.toStatement(it) }

        return@transaction orcamentoMensal.copy(
            id = id.value
        )
    }

    fun buscarPorId(id: Long): OrcamentoMensal? = transaction {
        val row = OrcamentosMensaisTable.selectAll()
            .where(OrcamentosMensaisTable.id eq id)
            .singleOrNull()

        row?.toOrcamentoMensal(
            lancamentos = null
        )
    }

    fun buscarLancamentosPorId(orcamentoMensal: OrcamentoMensal): OrcamentoMensal = transaction {
        OrcamentosMensaisTable.join(
            otherTable = LancamentosMensaisTable,
            joinType = INNER,
            onColumn = OrcamentosMensaisTable.id,
            otherColumn = LancamentosMensaisTable.orcamentoId
        ).selectAll()
            .where(OrcamentosMensaisTable.id eq orcamentoMensal.id)
            .map { row -> row.toLancamentoMensal() }
            .let { lancamentos -> orcamentoMensal.copy(lancamentos = lancamentos) }
    }

    fun atualizar(orcamentoMensal: OrcamentoMensal) = transaction {
        OrcamentosMensaisTable.update(where = { OrcamentosMensaisTable.id eq orcamentoMensal.id }) {
            orcamentoMensal.toStatement(it)
        }
    }
}