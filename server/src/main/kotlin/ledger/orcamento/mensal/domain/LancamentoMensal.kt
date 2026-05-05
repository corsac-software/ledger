package br.dev.brunorsch.ledger.orcamento.mensal.domain

import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.DESPESA
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.RECEITA
import java.math.BigDecimal

data class LancamentoMensal(
    val id: Long,
    val slug: String,
    var descricao: String,
    var valor: BigDecimal,
    var tipo: TipoLancamento,
    var statusDespesa: StatusDespesa?
) {
    enum class StatusDespesa {
        ABERTO,
        RESERVADO,
        PAGO
    }

    companion object {
        fun criarReceita(
            id: Long,
            slug: String,
            descricao: String,
            valor: BigDecimal
        ): LancamentoMensal {
            return LancamentoMensal(
                id = id,
                slug = slug,
                descricao = descricao,
                valor = valor,
                tipo = RECEITA,
                statusDespesa = null
            )
        }

        fun criarDespesa(
            id: Long,
            slug: String,
            descricao: String,
            valor: BigDecimal,
            statusDespesa: StatusDespesa
        ): LancamentoMensal {
            return LancamentoMensal(
                id = id,
                slug = slug,
                descricao = descricao,
                valor = valor,
                tipo = DESPESA,
                statusDespesa = statusDespesa
            )
        }
    }
}