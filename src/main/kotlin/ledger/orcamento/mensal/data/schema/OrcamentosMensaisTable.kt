package br.dev.brunorsch.ledger.orcamento.mensal.data.schema

import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import kotlinx.datetime.number
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.datetime.date

object OrcamentosMensaisTable : LongIdTable("orcamentos_mensais") {
    val usuarioId = long("usuario_id")

    val ano = integer("ano")
    val mes = integer("mes")

    val slug = varchar("slug", 7)

    val dataInicio = date("data_inicio")
    val dataFim = date("data_fim")

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
    lancamentos: List<LancamentoMensal>?
) = OrcamentoMensal(
    id = this[OrcamentosMensaisTable.id].value,
    idUsuario = this[OrcamentosMensaisTable.usuarioId],
    anoMes = this.toAnoMes(),
    dataInicio = this[OrcamentosMensaisTable.dataInicio],
    dataFim = this[OrcamentosMensaisTable.dataFim],
    lancamentos = lancamentos
)

fun OrcamentoMensal.toStatement(stmt: UpdateBuilder<*>) {
    stmt[OrcamentosMensaisTable.usuarioId] = this.idUsuario
    stmt[OrcamentosMensaisTable.ano] = this.anoMes.ano.value
    stmt[OrcamentosMensaisTable.mes] = this.anoMes.mes.number
    stmt[OrcamentosMensaisTable.slug] = this.slug
    stmt[OrcamentosMensaisTable.dataInicio] = this.dataInicio
    stmt[OrcamentosMensaisTable.dataFim] = this.dataFim
}
