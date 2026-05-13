package br.dev.corsac.ledger.orcamento.mensal.data.schema

import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.LancamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.LancamentoMensal.StatusDespesa
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.valueOf
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder


object LancamentosMensaisTable : LongIdTable("lancamentos_mensais") {
    val orcamentoId = long("orcamento_id")
        .references(OrcamentosMensaisTable.id)

    val slug = varchar("slug", 20)

    val descricao = varchar("descricao", 32)
    val valor = decimal("valor", 10, 2)

    val tipo = varchar("tipo", 10)
    val statusDespesa = varchar("status_despesa", 10).nullable()
    val faturaId = long("fatura_id").nullable()

    init {
        uniqueIndex(
            "uk_lancamento_orcamento_slug",
            orcamentoId,
            slug
        )
    }
}

fun ResultRow.toLancamentoMensal() = LancamentoMensal(
    id = this[LancamentosMensaisTable.id].value,
    slug = this[LancamentosMensaisTable.slug],
    descricao = this[LancamentosMensaisTable.descricao],
    valor = this[LancamentosMensaisTable.valor],
    tipo = valueOf(this[LancamentosMensaisTable.tipo]),
    statusDespesa = this[LancamentosMensaisTable.statusDespesa]
        ?.let { StatusDespesa.valueOf(it) },
    faturaId = this[LancamentosMensaisTable.faturaId]
)

fun LancamentoMensal.toStatement(stmt: UpdateBuilder<*>, orcamentoId: Long) {
    stmt[LancamentosMensaisTable.orcamentoId] =
        orcamentoId
    stmt[LancamentosMensaisTable.slug] = this.slug
    stmt[LancamentosMensaisTable.descricao] =
        this.descricao
    stmt[LancamentosMensaisTable.valor] =
        this.valor
    stmt[LancamentosMensaisTable.tipo] =
        this.tipo.name
    stmt[LancamentosMensaisTable.statusDespesa] =
        this.statusDespesa?.name
    stmt[LancamentosMensaisTable.faturaId] =
        this.faturaId
}
