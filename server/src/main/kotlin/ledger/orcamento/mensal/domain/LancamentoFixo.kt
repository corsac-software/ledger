package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class LancamentoFixo(
    val id: Long,
    val idUsuario: Long,
    val tipo: TipoLancamento,
    val descricao: String,
    val valor: BigDecimal,
    val diaVencimento: Int,
    val mesInicio: AnoMes,
    val formaPagamento: FormaPagamento,
    val idCartao: Long?,
    val idCategoria: Long,
    val ativo: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
    val excluidoEm: AnoMes?
) {
    enum class FormaPagamento {
        BOLETO,
        PIX,
        CARTAO
    }
}
