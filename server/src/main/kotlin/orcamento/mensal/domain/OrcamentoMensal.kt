package br.dev.corsac.ledger.orcamento.mensal.domain

import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.LancamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.DESPESA
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.RECEITA
import kotlinx.datetime.LocalDate

data class OrcamentoMensal(
    val id: Long,
    val idUsuario: Long,
    val anoMes: AnoMes,
    val slug: String = anoMes.toFormatoSlug(),
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    var seqReceita: Int = 0,
    var seqDespesa: Int = 0,
    val lancamentos: List<LancamentoMensal>? = null
) {
    fun proximoSlug(tipo: TipoLancamento): String {
        val proximoSeq = when (tipo) {
            RECEITA -> seqReceita + 1
            DESPESA -> seqDespesa + 1
        }
        return "${tipo.prefixoSlug}-${anoMes.anoAsString()}-${anoMes.mesAsString()}-$proximoSeq"
    }
}