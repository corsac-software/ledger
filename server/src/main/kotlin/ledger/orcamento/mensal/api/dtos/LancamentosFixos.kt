package br.dev.brunorsch.ledger.orcamento.mensal.api.dtos

import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoFixo
import br.dev.brunorsch.ledger.utils.BigDecimalJson
import kotlinx.serialization.Serializable

@Serializable
data class LancamentoFixoRequest(
    val tipo: String,
    val descricao: String,
    val valor: BigDecimalJson,
    val diaVencimento: Int,
    val mesInicio: String,
    val formaPagamento: String,
    val idCartao: Long? = null,
    val idCategoria: Long
)

@Serializable
data class LancamentoFixoUpdateRequest(
    val tipo: String? = null,
    val descricao: String? = null,
    val valor: BigDecimalJson? = null,
    val diaVencimento: Int? = null,
    val mesInicio: String? = null,
    val formaPagamento: String? = null,
    val idCartao: Long? = null,
    val idCategoria: Long? = null,
)

@Serializable
data class LancamentoFixoResponse(
    val id: Long,
    val idUsuario: Long,
    val tipo: String,
    val descricao: String,
    val valor: BigDecimalJson,
    val diaVencimento: Int,
    val mesInicio: String,
    val formaPagamento: String,
    val idCartao: Long?,
    val idCategoria: Long,
    val ativo: Boolean,
    val criadoEm: String,
    val atualizadoEm: String,
    val excluidoEm: String?
)

fun LancamentoFixo.toResponse() = LancamentoFixoResponse(
    id = id,
    idUsuario = idUsuario,
    tipo = tipo.name,
    descricao = descricao,
    valor = valor,
    diaVencimento = diaVencimento,
    mesInicio = mesInicio.toString(),
    formaPagamento = formaPagamento.name,
    idCartao = idCartao,
    idCategoria = idCategoria,
    ativo = ativo,
    criadoEm = criadoEm.toString(),
    atualizadoEm = atualizadoEm.toString(),
    excluidoEm = excluidoEm?.toString()
)
