package br.dev.corsac.ledger.orcamento.mensal.data.repository

import br.dev.corsac.ledger.orcamento.mensal.data.schema.CartoesTable
import br.dev.corsac.ledger.orcamento.mensal.data.schema.ParcelamentosTable
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toParcelamento
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.corsac.ledger.orcamento.mensal.domain.cartoes.Parcelamento
import br.dev.corsac.ledger.utils.now
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class ParcelamentosRepository {
    fun buscarTodos(idCartao: Long, idUsuario: Long): List<Parcelamento> = transaction {
        ParcelamentosTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (ParcelamentosTable.cartaoId eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true) and
                        (ParcelamentosTable.ativo eq true)
            }
            .map { it.toParcelamento() }
    }

    fun buscarPorId(id: Long, idCartao: Long, idUsuario: Long): Parcelamento? = transaction {
        ParcelamentosTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (ParcelamentosTable.id eq id) and
                        (ParcelamentosTable.cartaoId eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true) and
                        (ParcelamentosTable.ativo eq true)
            }
            .singleOrNull()
            ?.toParcelamento()
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

    fun criar(parcelamento: Parcelamento): Parcelamento = transaction {
        val id = ParcelamentosTable.insertAndGetId { parcelamento.toStatement(it) }
        parcelamento.copy(id = id.value)
    }

    fun atualizar(parcelamento: Parcelamento, idUsuario: Long): Parcelamento? = transaction {
        ParcelamentosTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (ParcelamentosTable.id eq parcelamento.id) and
                        (ParcelamentosTable.cartaoId eq parcelamento.idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .singleOrNull()
            ?: return@transaction null

        ParcelamentosTable.update({
            (ParcelamentosTable.id eq parcelamento.id) and
                    (ParcelamentosTable.cartaoId eq parcelamento.idCartao)
        }) { stmt ->
            parcelamento.toStatement(stmt)
        }

        ParcelamentosTable.selectAll()
            .where {
                (ParcelamentosTable.id eq parcelamento.id) and
                        (ParcelamentosTable.cartaoId eq parcelamento.idCartao)
            }
            .singleOrNull()
            ?.toParcelamento()
    }

    fun deletar(id: Long, idCartao: Long, idUsuario: Long): Result<Unit> = transaction {
        val excluidoEm = LocalDateTime.now()
        ParcelamentosTable
            .innerJoin(CartoesTable)
            .selectAll()
            .where {
                (ParcelamentosTable.id eq id) and
                        (ParcelamentosTable.cartaoId eq idCartao) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true) and
                        (ParcelamentosTable.ativo eq true)
            }
            .singleOrNull()
            ?: return@transaction Result.failure(NoSuchElementException("Parcelamento não encontrado"))

        val linhasAfetadas = ParcelamentosTable.update({
            (ParcelamentosTable.id eq id) and
                    (ParcelamentosTable.cartaoId eq idCartao) and
                    (ParcelamentosTable.ativo eq true)
        }) { stmt ->
            stmt[ParcelamentosTable.ativo] = false
            stmt[ParcelamentosTable.excluidoEm] = excluidoEm
            stmt[ParcelamentosTable.atualizadoEm] = excluidoEm
        }

        if (linhasAfetadas > 0) {
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("Parcelamento não encontrado"))
        }
    }
}
