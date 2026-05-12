package br.dev.brunorsch.ledger.orcamento.mensal.data.repository

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CategoriaUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.CategoriasTable
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toCategoria
import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.toStatement
import br.dev.brunorsch.ledger.orcamento.mensal.domain.Categoria
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class CategoriasRepository {
    fun buscarTodas(idUsuario: Long): List<Categoria> = transaction {
        CategoriasTable.selectAll()
            .where {
                (CategoriasTable.usuarioId eq idUsuario) and
                    (CategoriasTable.ativo eq true)
            }
            .map { it.toCategoria() }
    }

    fun existeAlguma(idUsuario: Long): Boolean = transaction {
        CategoriasTable.selectAll()
            .where { CategoriasTable.usuarioId eq idUsuario }
            .limit(1)
            .any()
    }

    fun criar(categoria: Categoria): Categoria = transaction {
        val id = CategoriasTable.insertAndGetId { categoria.toStatement(it) }
        categoria.copy(id = id.value)
    }

    fun criarTodas(categorias: List<Categoria>): List<Categoria> = transaction {
        categorias.map { categoria ->
            val id = CategoriasTable.insertAndGetId { categoria.toStatement(it) }
            categoria.copy(id = id.value)
        }
    }

    fun atualizar(id: Long, idUsuario: Long, request: CategoriaUpdateRequest): Categoria? = transaction {
        CategoriasTable.update({
            (CategoriasTable.id eq id) and
                (CategoriasTable.usuarioId eq idUsuario) and
                (CategoriasTable.ativo eq true)
        }) { stmt ->
            request.toStatement(stmt)
        }

        CategoriasTable.selectAll()
            .where {
                (CategoriasTable.id eq id) and
                    (CategoriasTable.usuarioId eq idUsuario) and
                    (CategoriasTable.ativo eq true)
            }
            .singleOrNull()
            ?.toCategoria()
    }

    fun deletar(id: Long, idUsuario: Long): Result<Unit> = transaction {
        val linhasAfetadas = CategoriasTable.update({
            (CategoriasTable.id eq id) and
                (CategoriasTable.usuarioId eq idUsuario) and
                (CategoriasTable.ativo eq true)
        }) { stmt ->
            stmt[CategoriasTable.ativo] = false
        }

        if (linhasAfetadas > 0) {
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("Categoria não encontrada"))
        }
    }
}
