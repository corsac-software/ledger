package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class Parcelamento(
    val id: Long,
    val idCartao: Long,
    val nome: String,
    val valor: BigDecimal,
    val parcelas: Int,
    val mesInicio: AnoMes,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
    val excluidoEm: LocalDateTime?
)
