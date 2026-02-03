package br.dev.brunorsch.orcamento.familiar.mensal.data

import br.dev.brunorsch.orcamento.familiar.mensal.AnoMes
import br.dev.brunorsch.orcamento.familiar.mensal.LancamentoMensal
import br.dev.brunorsch.orcamento.familiar.mensal.OrcamentoMensal
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date

object OrcamentosMensaisTable : Table("orcamentos_mensais") {
    val id = long("id").autoIncrement()

    val usuarioId = long("usuario_id")

    val ano = integer("ano")
    val mes = integer("mes")

    val slug = varchar("slug", 7)

    val dataInicio = date("data_inicio")
    val dataFim = date("data_fim")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(
            "uk_orcamento_usuario_slug",
            usuarioId,
            slug
        )
    }
}


fun ResultRow.toAnoMes(): AnoMes =
    AnoMes(
        ano = this[OrcamentosMensaisTable.ano],
        mes = this[OrcamentosMensaisTable.mes]
    )


fun ResultRow.toOrcamentoMensal(
    lancamentos: List<LancamentoMensal>
) = OrcamentoMensal(
    id = this[OrcamentosMensaisTable.id],
    anoMes = this.toAnoMes(),
    dataInicio = this[OrcamentosMensaisTable.dataInicio],
    dataFim = this[OrcamentosMensaisTable.dataFim],
    lancamentos = lancamentos
)
