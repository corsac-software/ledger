package br.dev.corsac.ledger.orcamento.mensal.data.repository

import br.dev.corsac.ledger.orcamento.mensal.data.schema.OrcamentosMensaisTable
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toOrcamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.corsac.ledger.orcamento.mensal.domain.OrcamentoMensal
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class OrcamentosMensaisRepository {
    fun buscarTodos(idUsuario: Long): List<OrcamentoMensal> = transaction {
        OrcamentosMensaisTable.selectAll()
            .where { OrcamentosMensaisTable.usuarioId eq idUsuario }
            .map { row ->
                row.toOrcamentoMensal(
                    lancamentos = null
                )
            }
    }

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

    fun existe(id: Long, idULong: Long): Boolean = transaction {
        OrcamentosMensaisTable.selectAll()
            .where {
                OrcamentosMensaisTable.id eq id
                OrcamentosMensaisTable.usuarioId eq idULong
            }
            .empty().not()
    }

    fun excluir(id: Long, idUsuario: Long) = transaction {
        OrcamentosMensaisTable.deleteWhere {
            OrcamentosMensaisTable.id eq id
            OrcamentosMensaisTable.usuarioId eq idUsuario
        }
    }

    fun atualizarSequencias(orcamentoId: Long, seqReceita: Int, seqDespesa: Int) = transaction {
        OrcamentosMensaisTable.update({ OrcamentosMensaisTable.id eq orcamentoId }) { stmt ->
            stmt[OrcamentosMensaisTable.seqReceita] = seqReceita
            stmt[OrcamentosMensaisTable.seqDespesa] = seqDespesa
        }
    }

    fun incrementarSeqReceita(orcamentoId: Long): Int =
        incrementarSeq(orcamentoId, OrcamentosMensaisTable.seqReceita)

    fun incrementarSeqDespesa(orcamentoId: Long): Int =
        incrementarSeq(orcamentoId, OrcamentosMensaisTable.seqDespesa)

    private fun incrementarSeq(orcamentoId: Long, seq: Column<Int>): Int = transaction {
        val orcamento = OrcamentosMensaisTable.selectAll()
            .where { OrcamentosMensaisTable.id eq orcamentoId }
            .single()

        val novoSeq = orcamento[seq] + 1
        OrcamentosMensaisTable.update({ OrcamentosMensaisTable.id eq orcamentoId }) { stmt ->
            stmt[seq] = novoSeq
        }
        novoSeq
    }
}
