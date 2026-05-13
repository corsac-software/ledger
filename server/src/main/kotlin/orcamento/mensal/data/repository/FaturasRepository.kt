package br.dev.corsac.ledger.orcamento.mensal.data.repository

import br.dev.corsac.ledger.orcamento.mensal.data.schema.*
import br.dev.corsac.ledger.orcamento.mensal.domain.cartoes.Fatura
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class FaturasRepository {
    fun buscarTodos(idCartao: Long, idUsuario: Long): List<Fatura> = transaction {
        FaturasTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (FaturasTable.cartaoId eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .map { it.toFatura() }
    }

    fun buscarPorId(id: Long, idCartao: Long, idUsuario: Long): Fatura? = transaction {
        FaturasTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (FaturasTable.id eq id) and
                        (FaturasTable.cartaoId eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .singleOrNull()
            ?.toFatura()
    }

    fun cartaoExiste(idCartao: Long, idUsuario: Long): Boolean = transaction {
        CartoesTable.selectAll()
            .where {
                (CartoesTable.id eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .limit(1)
            .any()
    }

    fun criar(fatura: Fatura): Fatura = transaction {
        val id = FaturasTable.insertAndGetId { fatura.toStatement(it) }
        fatura.copy(id = id.value)
    }

    fun atualizar(fatura: Fatura, idUsuario: Long): Fatura? = transaction {
        FaturasTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (FaturasTable.id eq fatura.id) and
                        (FaturasTable.cartaoId eq fatura.idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .singleOrNull()
            ?: return@transaction null

        FaturasTable.update({
            (FaturasTable.id eq fatura.id) and
                    (FaturasTable.cartaoId eq fatura.idCartao)
        }) { stmt ->
            fatura.toStatement(stmt)
        }

        FaturasTable.selectAll()
            .where {
                (FaturasTable.id eq fatura.id) and
                        (FaturasTable.cartaoId eq fatura.idCartao)
            }
            .singleOrNull()
            ?.toFatura()
    }

    fun deletar(id: Long, idCartao: Long, idUsuario: Long): Result<Unit> = transaction {
        FaturasTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (FaturasTable.id eq id) and
                        (FaturasTable.cartaoId eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .singleOrNull()
            ?: return@transaction Result.failure(NoSuchElementException("Fatura não encontrada"))

        FaturasTable.deleteWhere {
            (FaturasTable.id eq id) and
                    (FaturasTable.cartaoId eq idCartao)
        }

        Result.success(Unit)
    }

    fun atualizarFaturaIdLancamento(lancamentoId: Long, faturaId: Long?) = transaction {
        LancamentosMensaisTable.update({
            LancamentosMensaisTable.id eq lancamentoId
        }) { stmt ->
            stmt[LancamentosMensaisTable.faturaId] = faturaId
        }
    }
}