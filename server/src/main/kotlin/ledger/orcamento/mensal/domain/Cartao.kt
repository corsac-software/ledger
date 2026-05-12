package br.dev.brunorsch.ledger.orcamento.mensal.domain

import br.dev.brunorsch.ledger.utils.idNaoInserido
import br.dev.brunorsch.ledger.utils.now
import kotlinx.datetime.LocalDateTime

data class Cartao(
    val id: Long = idNaoInserido,
    val idUsuario: Long,
    val nome: String,
    val icone: String,
    val cor: String,
    val ativo: Boolean = true,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now(),
)
