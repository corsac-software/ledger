package br.dev.brunorsch.ledger.orcamento.mensal.domain

import br.dev.brunorsch.ledger.orcamento.mensal.data.schema.LancamentosMensaisTable.statusDespesa
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal.StatusDespesa.ABERTO
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.DESPESA
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.RECEITA
import br.dev.brunorsch.ledger.utils.idNaoInserido
import java.math.BigDecimal

data class LancamentoMensal(
    val id: Long,
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
            id: Long = idNaoInserido,
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
            id: Long = idNaoInserido,
            slug: String,
            descricao: String,
            valor: BigDecimal,
        ): LancamentoMensal {
            return LancamentoMensal(
                id = id,
                slug = slug,
                descricao = descricao,
                valor = valor,
                tipo = DESPESA,
                statusDespesa = ABERTO
            )
        }
    }
}