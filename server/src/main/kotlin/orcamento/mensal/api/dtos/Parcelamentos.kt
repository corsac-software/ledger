package br.dev.corsac.ledger.orcamento.mensal.api.dtos

import br.dev.corsac.ledger.orcamento.mensal.domain.cartoes.Parcelamento
import br.dev.corsac.ledger.utils.BigDecimalJson
import kotlinx.serialization.Serializable

@Serializable
data class ParcelamentoRequest(
    val nome: String,
    val valor: BigDecimalJson,
    val parcelas: Int,
    val mesInicio: String
)

@Serializable
data class ParcelamentoUpdateRequest(
    val nome: String? = null,
    val valor: BigDecimalJson? = null,
    val parcelas: Int? = null,
    val mesInicio: String? = null,
)

@Serializable
data class ParcelamentoResponse(
    val id: Long,
    val idCartao: Long,
    val nome: String,
    val valor: BigDecimalJson,
    val parcelas: Int,
    val mesInicio: String,
    val ativo: Boolean,
    val criadoEm: String,
    val atualizadoEm: String,
    val excluidoEm: String?
)

fun Parcelamento.toResponse() = ParcelamentoResponse(
    id = id,
    idCartao = idCartao,
    nome = nome,
    valor = valor,
    parcelas = parcelas,
    mesInicio = mesInicio.toString(),
    ativo = ativo,
    criadoEm = criadoEm.toString(),
    atualizadoEm = atualizadoEm.toString(),
    excluidoEm = excluidoEm?.toString()
)
