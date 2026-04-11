package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.utils.BigDecimalJson
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
    val seqReceita: Int = 0,
    val seqDespesa: Int = 0,
    val lancamentos: List<LancamentoResponse>? = null
)

@Serializable
data class LancamentoRequest(
    val descricao: String,
    val valor: BigDecimalJson,
    val tipo: String,
    val statusDespesa: String? = null
)

@Serializable
data class LancamentoUpdateRequest(
    val descricao: String? = null,
    val valor: BigDecimalJson? = null,
    val statusDespesa: String? = null
)