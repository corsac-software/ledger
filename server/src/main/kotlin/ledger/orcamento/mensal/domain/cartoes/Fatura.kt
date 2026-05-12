package br.dev.brunorsch.ledger.orcamento.mensal.domain.cartoes

import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.utils.idNaoInserido
import br.dev.brunorsch.ledger.utils.now
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class Fatura(
    val id: Long = idNaoInserido,
    val idCartao: Long,
    val idLancamento: Long,
    val valor: BigDecimal,
    val mes: AnoMes,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now(),
)