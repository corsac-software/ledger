package br.dev.brunorsch.orcamento.familiar.mensal

import kotlinx.datetime.LocalDate

class OrcamentoMensal(
    val id: Long,
    val anoMes: AnoMes,
    val slug: String = anoMes.toFormatoSlug(),
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val lancamentos: List<LancamentoMensal>
)