package br.dev.brunorsch.ledger.orcamento.mensal.domain

import br.dev.brunorsch.ledger.utils.idNaoInserido
import br.dev.brunorsch.ledger.utils.now
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class Parcelamento(
    val id: Long = idNaoInserido,
    val idCartao: Long,
    val nome: String,
    val valor: BigDecimal,
    val parcelas: Int,
    val mesInicio: AnoMes,
    val ativo: Boolean = true,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now(),
    val excluidoEm: LocalDateTime? = null,
)
