package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.LocalDateTime

data class Cartao(
    val id: Long,
    val idUsuario: Long,
    val nome: String,
    val icone: String,
    val cor: String,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime
)
