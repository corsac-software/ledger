package br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos

import br.dev.corsac.ledger.orcamento.mensal.domain.AnoMes
import br.dev.corsac.ledger.utils.idNaoInserido
import br.dev.corsac.ledger.utils.now
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class LancamentoFixo(
    val id: Long = idNaoInserido,
    val idUsuario: Long,
    val tipo: TipoLancamento,
    val descricao: String,
    val valor: BigDecimal,
    val diaVencimento: Int,
    val mesInicio: AnoMes,
    val formaPagamento: FormaPagamento,
    val idCartao: Long?,
    val idCategoria: Long,
    val ativo: Boolean = true,
    val criadoEm: LocalDateTime = LocalDateTime.now(),
    val atualizadoEm: LocalDateTime = LocalDateTime.now(),
    val excluidoEm: AnoMes? = null,
) {
    enum class FormaPagamento {
        BOLETO,
        PIX,
        CARTAO
    }
}
