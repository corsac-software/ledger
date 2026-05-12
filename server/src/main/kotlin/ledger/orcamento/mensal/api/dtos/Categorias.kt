package br.dev.brunorsch.ledger.orcamento.mensal.api.dtos

import br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos.Categoria
import kotlinx.serialization.Serializable

@Serializable
data class CategoriaRequest(
    val nome: String,
    val icone: String
)

@Serializable
data class CategoriaUpdateRequest(
    val nome: String? = null,
    val icone: String? = null
)

@Serializable
data class CategoriaResponse(
    val id: Long,
    val idUsuario: Long,
    val nome: String,
    val icone: String
)

fun Categoria.toResponse() = CategoriaResponse(
    id = id,
    idUsuario = idUsuario,
    nome = nome,
    icone = icone
)
