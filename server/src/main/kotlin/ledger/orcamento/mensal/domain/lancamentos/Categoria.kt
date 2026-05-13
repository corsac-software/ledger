package br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos

import br.dev.brunorsch.ledger.utils.idNaoInserido

data class Categoria(
    val id: Long = idNaoInserido,
    val idUsuario: Long,
    val nome: String,
    val icone: String,
    val ativo: Boolean = true
)
