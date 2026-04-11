package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.LocalDate

data class OrcamentoMensal(
    val id: Long,
    val idUsuario: Long,
    val anoMes: AnoMes,
    val slug: String = anoMes.toFormatoSlug(),
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val seqReceita: Int = 0,
    val seqDespesa: Int = 0,
    val lancamentos: List<LancamentoMensal>? = null
) {
    fun proximoSlug(tipo: LancamentoMensal.Tipo): String {
        val proximoSeq = when (tipo) {
            LancamentoMensal.Tipo.RECEITA -> seqReceita + 1
            LancamentoMensal.Tipo.DESPESA -> seqDespesa + 1
        }
        return "${tipo.prefixoSlug}-${anoMes.anoAsString()}-${anoMes.mesAsString()}-$proximoSeq"
    }
}