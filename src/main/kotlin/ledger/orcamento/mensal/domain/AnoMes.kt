package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.Month
import kotlinx.datetime.number
import java.time.Year


data class AnoMes(
    val ano: Year,
    val mes: Month,
) {
    constructor(ano: Int, mes: Int) : this(
        mes = Month(mes),
        ano = Year.of(ano),
    )

    fun mesAsString() = mes.number.toString().padStart(2, '0')

    fun anoAsString() = ano.value.toString()

    override fun toString() = "${anoAsString()}-${mesAsString()}"

    fun toFormatoSlug() = "${anoAsString()}${mesAsString()}"
}