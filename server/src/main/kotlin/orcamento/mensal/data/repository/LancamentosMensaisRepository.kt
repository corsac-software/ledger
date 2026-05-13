package br.dev.corsac.ledger.orcamento.mensal.data.repository

import br.dev.corsac.ledger.orcamento.mensal.data.schema.LancamentosMensaisTable
import br.dev.corsac.ledger.orcamento.mensal.data.schema.OrcamentosMensaisTable
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toLancamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.corsac.ledger.orcamento.mensal.domain.OrcamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.LancamentoMensal
import org.jetbrains.exposed.v1.core.JoinType.INNER
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal

class LancamentosMensaisRepository {
    fun buscarPorOrcamento(orcamentoMensal: OrcamentoMensal): OrcamentoMensal = transaction {
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

    fun buscarPorOrcamentoId(id: Long, idUsuario: Long): List<LancamentoMensal> = transaction {
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

    fun buscarPorId(id: Long, orcamentoId: Long, idUsuario: Long): LancamentoMensal? = transaction {
        LancamentosMensaisTable.join(
            otherTable = OrcamentosMensaisTable,
            joinType = INNER,
            onColumn = LancamentosMensaisTable.orcamentoId,
            otherColumn = OrcamentosMensaisTable.id,
        ).select(LancamentosMensaisTable.columns)
            .where {
                LancamentosMensaisTable.id eq id
                LancamentosMensaisTable.orcamentoId eq orcamentoId
                OrcamentosMensaisTable.usuarioId eq idUsuario
            }
            .map { it.toLancamentoMensal() }
            .singleOrNull()
    }

    fun criar(orcamentoId: Long, lancamento: LancamentoMensal): LancamentoMensal = transaction {
        val id = LancamentosMensaisTable.insertAndGetId { stmt ->
            lancamento.toStatement(stmt, orcamentoId)
        }
        return@transaction lancamento.copy(id = id.value)
    }

    fun criarBatch(orcamentoId: Long, lancamentos: List<LancamentoMensal>): List<LancamentoMensal> = transaction {
        if (lancamentos.isEmpty()) return@transaction emptyList()

        val ids = LancamentosMensaisTable.batchInsert(lancamentos) { lancamento ->
            lancamento.toStatement(this, orcamentoId)
        }.map { it[LancamentosMensaisTable.id].value }

        lancamentos.zip(ids) { lancamento, id -> lancamento.copy(id = id) }
    }

    fun atualizar(id: Long, descricao: String?, valor: BigDecimal?, statusDespesa: String? = null) = transaction {
        LancamentosMensaisTable.update({ LancamentosMensaisTable.id eq id }) { stmt ->
            descricao?.let { stmt[LancamentosMensaisTable.descricao] = it }
            valor?.let { stmt[LancamentosMensaisTable.valor] = it }
            statusDespesa?.let { stmt[LancamentosMensaisTable.statusDespesa] = it }
        }
    }

    fun excluir(id: Long, orcamentoId: Long) = transaction {
        LancamentosMensaisTable.deleteWhere {
            LancamentosMensaisTable.id eq id
            LancamentosMensaisTable.orcamentoId eq orcamentoId
        }
    }

    fun existePorId(id: Long, orcamentoId: Long, idUsuario: Long): Boolean = transaction {
        LancamentosMensaisTable.join(
            otherTable = OrcamentosMensaisTable,
            joinType = INNER,
            onColumn = LancamentosMensaisTable.orcamentoId,
            otherColumn = OrcamentosMensaisTable.id,
        ).select(LancamentosMensaisTable.columns)
            .where {
                LancamentosMensaisTable.id eq id
                LancamentosMensaisTable.orcamentoId eq orcamentoId
                OrcamentosMensaisTable.usuarioId eq idUsuario
            }
            .empty().not()
    }
}
