package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlin.time.Instant

data class LancamentoFixo(
    val id: Long,
    val idUsuario: Long,
    val tipo: TipoLancamento,
    val diaVencimento: Int,
    val mesInicio: AnoMes,
    val formaPagamento: FormaPagamento,
    val idCartao: Long?,
    val idCategoria: Long,
    val ativo: Boolean,
    val criadoEm: Instant,
    val atualizadoEm: Instant,
    val excluidoEm: AnoMes?
) {
    enum class FormaPagamento {
        BOLETO,
        PIX,
        CARTAO
    }
}
