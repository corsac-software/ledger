package br.dev.brunorsch.ledger.orcamento.mensal.domain

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import java.math.BigDecimal

data class LancamentoMensal(
    val id: Long,
    val slug: String,
    var descricao: String,
    var valor: BigDecimal,
    var tipo: Tipo,
    var statusDespesa: StatusDespesa?
) {
    enum class Tipo(val prefixoSlug: String) {
        RECEITA("R"),
        DESPESA("D")
    }

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
                tipo = Tipo.RECEITA,
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
                tipo = Tipo.DESPESA,
                statusDespesa = statusDespesa
            )
        }
    }
}