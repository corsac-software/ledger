package br.dev.brunorsch.ledger.orcamento.mensal.domain

data class Categoria(
    val id: Long,
    val idUsuario: Long,
    val nome: String,
    val icone: String,
    val ativo: Boolean
)
