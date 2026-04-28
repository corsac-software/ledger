package br.dev.brunorsch.ledger.orcamento.mensal.domain

import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Year

object YearSerializer : KSerializer<Year> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Year", PrimitiveKind.INT)
    override fun serialize(encoder: Encoder, value: Year) = encoder.encodeInt(value.value)
    override fun deserialize(decoder: Decoder): Year = Year.of(decoder.decodeInt())
}

@Serializable
data class AnoMes(
    @Serializable(with = YearSerializer::class)
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

fun String.toAnoMes(): AnoMes {
    require(matches(Regex("^\\d{6}$"))) { "anoMes deve estar no formato AAAAMM" }
    val ano = substring(0, 4).toInt()
    val mes = substring(4, 6).toInt()
    require(mes in 1..12) { "anoMes possui mês inválido" }
    return AnoMes(ano, mes)
}