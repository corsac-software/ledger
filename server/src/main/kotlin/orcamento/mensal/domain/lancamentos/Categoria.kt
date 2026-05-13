package br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos

import br.dev.corsac.ledger.utils.idNaoInserido

data class Categoria(
    val id: Long = idNaoInserido,
    val idUsuario: Long,
    val nome: String,
    val icone: String,
    val ativo: Boolean = true
)
