package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.LocalDate

data class OrcamentoMensal(
    val id: Long,
    val idUsuario: Long,
    val anoMes: AnoMes,
    val slug: String = anoMes.toFormatoSlug(),
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val lancamentos: List<LancamentoMensal>? = null
)