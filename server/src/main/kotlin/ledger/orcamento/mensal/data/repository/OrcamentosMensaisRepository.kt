package br.dev.brunorsch.ledger.orcamento.mensal.data.repository

import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.*
import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoFixo
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.JoinType.INNER
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.math.BigDecimal

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

    fun buscarLancamentoPorId(id: Long, orcamentoId: Long, idUsuario: Long): LancamentoMensal? = transaction {
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

    fun criarLancamento(orcamentoId: Long, lancamento: LancamentoMensal): LancamentoMensal = transaction {
        val id = LancamentosMensaisTable.insertAndGetId { stmt ->
            stmt[LancamentosMensaisTable.orcamentoId] = orcamentoId
            stmt[LancamentosMensaisTable.slug] = lancamento.slug
            stmt[LancamentosMensaisTable.descricao] = lancamento.descricao
            stmt[LancamentosMensaisTable.valor] = lancamento.valor
            stmt[LancamentosMensaisTable.tipo] = lancamento.tipo.name
            stmt[LancamentosMensaisTable.statusDespesa] = lancamento.statusDespesa?.name
        }
        return@transaction lancamento.copy(id = id.value)
    }

    fun buscarLancamentosFixosParaImportacao(idUsuario: Long, anoMes: AnoMes): List<LancamentoFixo> = transaction {
        val anoMesSlug = anoMes.toFormatoSlug()
        LancamentosFixosTable.selectAll()
            .where {
                (LancamentosFixosTable.usuarioId eq idUsuario) and
                    (LancamentosFixosTable.mesInicio lessEq anoMesSlug) and
                    (
                        (LancamentosFixosTable.ativo eq true) or
                            (LancamentosFixosTable.excluidoEm greaterEq anoMesSlug)
                    )
            }
            .orderBy(LancamentosFixosTable.id to SortOrder.ASC)
            .map { it.toLancamentoFixo() }
    }

    fun criarLancamentosBatch(orcamentoId: Long, lancamentos: List<LancamentoMensal>): List<LancamentoMensal> = transaction {
        if (lancamentos.isEmpty()) return@transaction emptyList()

        val ids = LancamentosMensaisTable.batchInsert(lancamentos) { lancamento ->
            this[LancamentosMensaisTable.orcamentoId] = orcamentoId
            this[LancamentosMensaisTable.slug] = lancamento.slug
            this[LancamentosMensaisTable.descricao] = lancamento.descricao
            this[LancamentosMensaisTable.valor] = lancamento.valor
            this[LancamentosMensaisTable.tipo] = lancamento.tipo.name
            this[LancamentosMensaisTable.statusDespesa] = lancamento.statusDespesa?.name
        }.map { it[LancamentosMensaisTable.id].value }

        lancamentos.zip(ids) { lancamento, id -> lancamento.copy(id = id) }
    }

    fun atualizarSequencias(orcamentoId: Long, seqReceita: Int, seqDespesa: Int) = transaction {
        OrcamentosMensaisTable.update({ OrcamentosMensaisTable.id eq orcamentoId }) { stmt ->
            stmt[OrcamentosMensaisTable.seqReceita] = seqReceita
            stmt[OrcamentosMensaisTable.seqDespesa] = seqDespesa
        }
    }

    fun atualizarLancamento(id: Long, descricao: String?, valor: BigDecimal?, statusDespesa: String?) = transaction {
        LancamentosMensaisTable.update({ LancamentosMensaisTable.id eq id }) { stmt ->
            descricao?.let { stmt[LancamentosMensaisTable.descricao] = it }
            valor?.let { stmt[LancamentosMensaisTable.valor] = it }
            statusDespesa?.let { stmt[LancamentosMensaisTable.statusDespesa] = it }
        }
    }

    fun excluirLancamento(id: Long, orcamentoId: Long) = transaction {
        LancamentosMensaisTable.deleteWhere {
            LancamentosMensaisTable.id eq id
            LancamentosMensaisTable.orcamentoId eq orcamentoId
        }
    }

    fun incrementarSeqReceita(orcamentoId: Long): Int = transaction {
        val orcamento = OrcamentosMensaisTable.selectAll()
            .where { OrcamentosMensaisTable.id eq orcamentoId }
            .single()
        val novoSeq = orcamento[OrcamentosMensaisTable.seqReceita] + 1
        OrcamentosMensaisTable.update({ OrcamentosMensaisTable.id eq orcamentoId }) { stmt ->
            stmt[OrcamentosMensaisTable.seqReceita] = novoSeq
        }
        novoSeq
    }

    fun incrementarSeqDespesa(orcamentoId: Long): Int = transaction {
        val orcamento = OrcamentosMensaisTable.selectAll()
            .where { OrcamentosMensaisTable.id eq orcamentoId }
            .single()
        val novoSeq = orcamento[OrcamentosMensaisTable.seqDespesa] + 1
        OrcamentosMensaisTable.update({ OrcamentosMensaisTable.id eq orcamentoId }) { stmt ->
            stmt[OrcamentosMensaisTable.seqDespesa] = novoSeq
        }
        novoSeq
    }

    fun existeLancamentoPorId(id: Long, orcamentoId: Long, idUsuario: Long): Boolean = transaction {
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
