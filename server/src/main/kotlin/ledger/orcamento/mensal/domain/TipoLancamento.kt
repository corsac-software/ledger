package br.dev.brunorsch.ledger.orcamento.mensal.domain

enum class TipoLancamento(val prefixoSlug: String) {
    RECEITA("R"),
    DESPESA("D")
}