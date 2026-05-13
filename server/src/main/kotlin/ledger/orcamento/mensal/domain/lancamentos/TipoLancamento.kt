package br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos

enum class TipoLancamento(val prefixoSlug: String) {
    RECEITA("R"),
    DESPESA("D")
}