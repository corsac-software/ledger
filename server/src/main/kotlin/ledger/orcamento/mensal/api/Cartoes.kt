package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.domain.Cartao
import kotlinx.serialization.Serializable

@Serializable
data class CartaoRequest(
    val nome: String,
    val icone: String,
    val cor: String
)

@Serializable
data class CartaoUpdateRequest(
    val nome: String? = null,
    val icone: String? = null,
    val cor: String? = null,
    val ativo: Boolean? = null
)

@Serializable
data class CartaoResponse(
    val id: Long,
    val idUsuario: Long,
    val nome: String,
    val icone: String,
    val cor: String,
    val ativo: Boolean,
    val criadoEm: String,
    val atualizadoEm: String
)

fun Cartao.toResponse() = CartaoResponse(
    id = id,
    idUsuario = idUsuario,
    nome = nome,
    icone = icone,
    cor = cor,
    ativo = ativo,
    criadoEm = criadoEm.toString(),
    atualizadoEm = atualizadoEm.toString()
)
