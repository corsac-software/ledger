package br.dev.corsac.ledger.orcamento.mensal.data.repository

import br.dev.corsac.ledger.orcamento.mensal.data.schema.CartoesTable
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toCartao
import br.dev.corsac.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.corsac.ledger.orcamento.mensal.domain.cartoes.Cartao
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class CartoesRepository {
    fun buscarTodos(idUsuario: Long): List<Cartao> = transaction {
        CartoesTable.selectAll()
            .where {
                (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .map { it.toCartao() }
    }

    fun buscarPorId(id: Long, idUsuario: Long): Cartao? = transaction {
        CartoesTable.selectAll()
            .where {
                (CartoesTable.id eq id) and
                        (CartoesTable.usuarioId eq idUsuario) and
                        (CartoesTable.ativo eq true)
            }
            .singleOrNull()
            ?.toCartao()
    }

    fun criar(cartao: Cartao): Cartao = transaction {
        val id = CartoesTable.insertAndGetId { cartao.toStatement(it) }
        cartao.copy(id = id.value)
    }

    fun atualizar(cartao: Cartao): Cartao? = transaction {
        CartoesTable.update({
            (CartoesTable.id eq cartao.id) and
                    (CartoesTable.usuarioId eq cartao.idUsuario)
        }) { stmt ->
            cartao.toStatement(stmt)
        }

        CartoesTable.selectAll()
            .where {
                (CartoesTable.id eq cartao.id) and
                        (CartoesTable.usuarioId eq cartao.idUsuario)
            }
            .singleOrNull()
            ?.toCartao()
    }

    fun deletar(id: Long, idUsuario: Long): Result<Unit> = transaction {
        val linhasAfetadas = CartoesTable.update({
            (CartoesTable.id eq id) and
                    (CartoesTable.usuarioId eq idUsuario) and
                    (CartoesTable.ativo eq true)
        }) { stmt ->
            stmt[CartoesTable.ativo] = false
        }

        if (linhasAfetadas > 0) {
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("Cartão não encontrado"))
        }
    }
}
