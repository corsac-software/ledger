package br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos

enum class TipoLancamento(val prefixoSlug: String) {
    RECEITA("R"),
    DESPESA("D")
}