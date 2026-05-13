package br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos

import br.dev.corsac.ledger.orcamento.mensal.domain.OrcamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.LancamentoMensal.StatusDespesa.ABERTO
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.DESPESA
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.RECEITA
import br.dev.corsac.ledger.utils.idNaoInserido
import java.math.BigDecimal

data class LancamentoMensal(
    val id: Long = idNaoInserido,
    val slug: String,
    var descricao: String,
    var valor: BigDecimal,
    var tipo: TipoLancamento,
    var statusDespesa: StatusDespesa?,
    val faturaId: Long? = null
) {
    enum class StatusDespesa {
        ABERTO,
        RESERVADO,
        PAGO
    }

    companion object {
        fun criarReceita(
            orcamento: OrcamentoMensal,
            descricao: String,
            valor: BigDecimal,
        ): LancamentoMensal {
            return criarReceita(
                slug = orcamento.proximoSlug(RECEITA),
                descricao = descricao,
                valor = valor
            )
        }

        private fun criarReceita(
            slug: String,
            descricao: String,
            valor: BigDecimal
        ): LancamentoMensal {
            return LancamentoMensal(
                slug = slug,
                descricao = descricao,
                valor = valor,
                tipo = RECEITA,
                statusDespesa = null
            )
        }

        fun criarDespesa(
            orcamento: OrcamentoMensal,
            descricao: String,
            valor: BigDecimal,
        ): LancamentoMensal {
            return criarDespesa(
                slug = orcamento.proximoSlug(DESPESA),
                descricao = descricao,
                valor = valor,
            )
        }

        private fun criarDespesa(
            slug: String,
            descricao: String,
            valor: BigDecimal,
        ): LancamentoMensal {
            return LancamentoMensal(
                slug = slug,
                descricao = descricao,
                valor = valor,
                tipo = DESPESA,
                statusDespesa = ABERTO
            )
        }
    }
}