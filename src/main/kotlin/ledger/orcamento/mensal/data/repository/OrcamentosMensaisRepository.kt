package br.dev.brunorsch.ledger.orcamento.mensal.data.repository

import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.*
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import org.jetbrains.exposed.v1.core.JoinType.INNER
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class OrcamentosMensaisRepository {
    fun criar(orcamentoMensal: OrcamentoMensal): OrcamentoMensal = transaction {
        val id = OrcamentosMensaisTable.insertAndGetId { orcamentoMensal.toStatement(it) }

        return@transaction orcamentoMensal.copy(
            id = id.value
        )
    }

    fun buscarPorId(id: Long, idUsuario: Long): OrcamentoMensal? = transaction {
        val row = OrcamentosMensaisTable.selectAll()
            .where {
                OrcamentosMensaisTable.id eq id
                OrcamentosMensaisTable.usuarioId eq idUsuario
            }
            .singleOrNull()

        row?.toOrcamentoMensal(
            lancamentos = null
        )
    }

    fun buscarLancamentos(orcamentoMensal: OrcamentoMensal): OrcamentoMensal = transaction {
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

    fun buscarLancamentosPorId(id: Long, idUsuario: Long): List<LancamentoMensal> = transaction {
        LancamentosMensaisTable.join(
            otherTable = OrcamentosMensaisTable,
            joinType = INNER,
            onColumn = LancamentosMensaisTable.orcamentoId,
            otherColumn = OrcamentosMensaisTable.id,
        ).select(LancamentosMensaisTable.columns)
            .where {
                OrcamentosMensaisTable.id eq id
                OrcamentosMensaisTable.usuarioId eq idUsuario
            }
            .map { it.toLancamentoMensal() }
    }
    
    fun excluir(id: Long, idUsuario: Long) = transaction {
        OrcamentosMensaisTable.deleteWhere {
            OrcamentosMensaisTable.id eq id
            OrcamentosMensaisTable.usuarioId eq idUsuario
        }
    }
}