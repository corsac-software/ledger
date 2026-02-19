package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal.StatusDespesa
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

@Serializable
data class LancamentoResponse(
    val id: Long,
    val slug: String,
    val descricao: String,
    val valor: Double,
    val tipo: String,
    val statusDespesa: StatusDespesa?
)

fun OrcamentoMensal.toResponse() = OrcamentoMensalResponse(
    id = id,
    idUsuario = idUsuario,
    ano = anoMes.ano.value,
    mes = anoMes.mes.number,
    slug = slug,
    dataInicio = dataInicio,
    dataFim = dataFim,
    lancamentos = lancamentos?.map { it.toResponse() }
)

fun LancamentoMensal.toResponse() = LancamentoResponse(
    id = id,
    slug = slug,
    descricao = descricao,
    valor = valor.toDouble(),
    tipo = tipo.name,
    statusDespesa = statusDespesa
)