package br.dev.brunorsch.orcamento.familiar.mensal.data

import br.dev.brunorsch.orcamento.familiar.mensal.LancamentoMensal
import br.dev.brunorsch.orcamento.familiar.mensal.LancamentoMensal.StatusDespesa
import br.dev.brunorsch.orcamento.familiar.mensal.LancamentoMensal.Tipo
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table


object LancamentosMensaisTable : Table("lancamentos_mensais") {
    val id = long("id").autoIncrement()

    val orcamentoId = long("orcamento_id")
        .references(OrcamentosMensaisTable.id)

    val slug = varchar("slug", 20)

    val descricao = varchar("descricao", 32)
    val valor = decimal("valor", 10, 2)

    val tipo = varchar("tipo", 10)
    val statusDespesa = varchar("status_despesa", 10).nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex("uk_lancamento_orcamento_slug", orcamentoId, slug)
    }
}

fun ResultRow.toLancamentoMensal() = LancamentoMensal(
    id = this[LancamentosMensaisTable.id],
    slug = this[LancamentosMensaisTable.slug],
    descricao = this[LancamentosMensaisTable.descricao],
    valor = this[LancamentosMensaisTable.valor],
    tipo = Tipo.valueOf(this[LancamentosMensaisTable.tipo]),
    statusDespesa = this[LancamentosMensaisTable.statusDespesa]
        ?.let { StatusDespesa.valueOf(it) }
)
