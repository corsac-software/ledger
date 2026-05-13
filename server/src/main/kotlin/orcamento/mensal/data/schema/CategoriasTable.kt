package br.dev.corsac.ledger.orcamento.mensal.data.schema

import br.dev.corsac.ledger.orcamento.mensal.api.dtos.CategoriaUpdateRequest
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.Categoria
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

object CategoriasTable : LongIdTable("categorias") {
    val usuarioId = long("usuario_id")
    val nome = varchar("nome", 16)
    val icone = varchar("icone", 16)
    val ativo = bool("ativo").default(true)

    init {
        uniqueIndex("uk_categoria_usuario_nome", usuarioId, nome)
    }
}

fun ResultRow.toCategoria() = Categoria(
    id = this[CategoriasTable.id].value,
    idUsuario = this[CategoriasTable.usuarioId],
    nome = this[CategoriasTable.nome],
    icone = this[CategoriasTable.icone],
    ativo = this[CategoriasTable.ativo]
)

fun Categoria.toStatement(stmt: UpdateBuilder<*>) {
    stmt[CategoriasTable.usuarioId] =
        this.idUsuario
    stmt[CategoriasTable.nome] = this.nome
    stmt[CategoriasTable.icone] = this.icone
    stmt[CategoriasTable.ativo] = this.ativo
}

fun CategoriaUpdateRequest.toStatement(stmt: UpdateBuilder<*>) {
    this.nome?.let {
        stmt[CategoriasTable.nome] = it
    }
    this.icone?.let {
        stmt[CategoriasTable.icone] = it
    }
}
