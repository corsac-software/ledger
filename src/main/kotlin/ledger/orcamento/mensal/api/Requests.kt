package br.dev.brunorsch.ledger.orcamento.mensal.api

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class OrcamentoMensalRequest(
    val idUsuario: Long,
    val ano: Int,
    val mes: Int,
    val dataInicio: LocalDate,
    val dataFim: LocalDate
)

@Serializable
data class OrcamentoMensalUpdateRequest(
    val ano: Int?,
    val mes: Int?,
    val dataInicio: LocalDate?,
    val dataFim: LocalDate?
)

@Serializable
data class OrcamentoMensalResponse(
    val id: Long,
    val idUsuario: Long,
    val ano: Int,
    val mes: Int,
    val slug: String,
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val lancamentos: List<LancamentoResponse>? = null
)