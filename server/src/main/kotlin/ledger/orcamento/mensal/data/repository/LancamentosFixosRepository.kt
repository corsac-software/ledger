package br.dev.brunorsch.ledger.orcamento.mensal.data.repository

import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.CategoriasTable
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.LancamentosFixosTable
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toLancamentoFixo
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos.LancamentoFixo
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class LancamentosFixosRepository {
    fun buscarTodos(idUsuario: Long): List<LancamentoFixo> = transaction {
        LancamentosFixosTable.selectAll()
            .where {
                (LancamentosFixosTable.usuarioId eq idUsuario) and
                        (LancamentosFixosTable.ativo eq true)
            }
            .map { it.toLancamentoFixo() }
    }

    fun buscarPorId(id: Long, idUsuario: Long): LancamentoFixo? = transaction {
        LancamentosFixosTable.selectAll()
            .where {
                (LancamentosFixosTable.id eq id) and
                        (LancamentosFixosTable.usuarioId eq idUsuario) and
                        (LancamentosFixosTable.ativo eq true)
            }
            .singleOrNull()
            ?.toLancamentoFixo()
    }

    fun categoriaExiste(idCategoria: Long, idUsuario: Long): Boolean = transaction {
        CategoriasTable.selectAll()
            .where {
                (CategoriasTable.id eq idCategoria) and
                        (CategoriasTable.usuarioId eq idUsuario) and
                        (CategoriasTable.ativo eq true)
            }
            .limit(1)
            .any()
    }

    fun buscarParaImportacao(idUsuario: Long, anoMes: AnoMes): List<LancamentoFixo> = transaction {
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

    fun criar(lancamentoFixo: LancamentoFixo): LancamentoFixo = transaction {
        val id = LancamentosFixosTable.insertAndGetId { lancamentoFixo.toStatement(it) }
        lancamentoFixo.copy(id = id.value)
    }

    fun atualizar(lancamentoFixo: LancamentoFixo): LancamentoFixo? = transaction {
        LancamentosFixosTable.update({
            (LancamentosFixosTable.id eq lancamentoFixo.id) and
                    (LancamentosFixosTable.usuarioId eq lancamentoFixo.idUsuario)
        }) { stmt ->
            lancamentoFixo.toStatement(stmt)
        }

        LancamentosFixosTable.selectAll()
            .where {
                (LancamentosFixosTable.id eq lancamentoFixo.id) and
                        (LancamentosFixosTable.usuarioId eq lancamentoFixo.idUsuario)
            }
            .singleOrNull()
            ?.toLancamentoFixo()
    }

    fun deletar(id: Long, idUsuario: Long, excluidoEm: AnoMes, atualizadoEm: LocalDateTime): Result<Unit> =
        transaction {
            val linhasAfetadas = LancamentosFixosTable.update({
                (LancamentosFixosTable.id eq id) and
                        (LancamentosFixosTable.usuarioId eq idUsuario) and
                        (LancamentosFixosTable.ativo eq true)
            }) { stmt ->
                stmt[LancamentosFixosTable.ativo] = false
                stmt[LancamentosFixosTable.excluidoEm] = excluidoEm.toFormatoSlug()
                stmt[LancamentosFixosTable.atualizadoEm] = atualizadoEm
            }

            if (linhasAfetadas > 0) {
                Result.success(Unit)
            } else {
                Result.failure(NoSuchElementException("Lançamento fixo não encontrado"))
            }
        }
}
