package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.domain.Fatura
import br.dev.brunorsch.ledger.utils.BigDecimalJson
import kotlinx.serialization.Serializable

@Serializable
data class FaturaRequest(
    val orcamentoId: Long,
    val valor: BigDecimalJson,
    val mes: String,
    val descricao: String
)

@Serializable
data class FaturaUpdateRequest(
    val valor: BigDecimalJson? = null,
    val mes: String? = null,
    val descricao: String? = null
)

@Serializable
data class FaturaResponse(
    val id: Long,
    val idCartao: Long,
    val idLancamento: Long,
    val valor: BigDecimalJson,
    val mes: String,
    val criadoEm: String,
    val atualizadoEm: String
)

fun Fatura.toResponse() = FaturaResponse(
    id = id,
    idCartao = idCartao,
    idLancamento = idLancamento,
    valor = valor,
    mes = mes.toString(),
    criadoEm = criadoEm.toString(),
    atualizadoEm = atualizadoEm.toString()
)