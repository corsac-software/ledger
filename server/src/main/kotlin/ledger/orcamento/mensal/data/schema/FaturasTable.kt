package br.dev.brunorsch.ledger.orcamento.mensal.data.schema

import br.dev.brunorsch.ledger.orcamento.mensal.domain.cartoes.Fatura
import br.dev.brunorsch.ledger.orcamento.mensal.domain.toAnoMes
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.datetime.datetime

object FaturasTable : LongIdTable("faturas") {
    val cartaoId = long("cartao_id")
        .references(CartoesTable.id)
    val lancamentoId = long("lancamento_id")
        .references(LancamentosMensaisTable.id)

    init {
        uniqueIndex("uk_fatura_lancamento", lancamentoId)
    }

    val valor = decimal("valor", 10, 2)
    val mes = varchar("mes", 6)
    val criadoEm = datetime("criado_em")
    val atualizadoEm = datetime("atualizado_em")
}

fun ResultRow.toFatura() = Fatura(
    id = this[FaturasTable.id].value,
    idCartao = this[FaturasTable.cartaoId],
    idLancamento = this[FaturasTable.lancamentoId],
    valor = this[FaturasTable.valor],
    mes = this[FaturasTable.mes].toAnoMes(),
    criadoEm = this[FaturasTable.criadoEm],
    atualizadoEm = this[FaturasTable.atualizadoEm]
)

fun Fatura.toStatement(stmt: UpdateBuilder<*>) {
    stmt[FaturasTable.cartaoId] = this.idCartao
    stmt[FaturasTable.lancamentoId] = this.idLancamento
    stmt[FaturasTable.valor] = this.valor
    stmt[FaturasTable.mes] = this.mes.toFormatoSlug()
    stmt[FaturasTable.criadoEm] = this.criadoEm
    stmt[FaturasTable.atualizadoEm] = this.atualizadoEm
}