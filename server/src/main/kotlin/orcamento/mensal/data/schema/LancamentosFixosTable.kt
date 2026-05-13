package br.dev.corsac.ledger.orcamento.mensal.data.schema

import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.LancamentoFixo
import br.dev.corsac.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.valueOf
import br.dev.corsac.ledger.orcamento.mensal.domain.toAnoMes
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.datetime.datetime

object LancamentosFixosTable : LongIdTable("lancamentos_fixos") {
    val usuarioId = long("usuario_id")
    val tipo = varchar("tipo", 16)
    val descricao = varchar("descricao", 32)
    val valor = decimal("valor", 10, 2)
    val diaVencimento = integer("dia_vencimento")
    val mesInicio = varchar("mes_inicio", 6)
    val formaPagamento = varchar("forma_pagamento", 16)
    val cartaoId = long("cartao_id").nullable()
    val categoriaId = long("categoria_id")
        .references(CategoriasTable.id)
    val ativo = bool("ativo").default(true)
    val criadoEm = datetime("criado_em")
    val atualizadoEm = datetime("atualizado_em")
    val excluidoEm = varchar("excluido_em", 6).nullable()
}

fun ResultRow.toLancamentoFixo() = LancamentoFixo(
    id = this[LancamentosFixosTable.id].value,
    idUsuario = this[LancamentosFixosTable.usuarioId],
    tipo = valueOf(this[LancamentosFixosTable.tipo]),
    descricao = this[LancamentosFixosTable.descricao],
    valor = this[LancamentosFixosTable.valor],
    diaVencimento = this[LancamentosFixosTable.diaVencimento],
    mesInicio = this[LancamentosFixosTable.mesInicio].toAnoMes(),
    formaPagamento = LancamentoFixo.FormaPagamento.valueOf(this[LancamentosFixosTable.formaPagamento]),
    idCartao = this[LancamentosFixosTable.cartaoId],
    idCategoria = this[LancamentosFixosTable.categoriaId],
    ativo = this[LancamentosFixosTable.ativo],
    criadoEm = this[LancamentosFixosTable.criadoEm],
    atualizadoEm = this[LancamentosFixosTable.atualizadoEm],
    excluidoEm = this[LancamentosFixosTable.excluidoEm]?.toAnoMes()
)

fun LancamentoFixo.toStatement(stmt: UpdateBuilder<*>) {
    stmt[LancamentosFixosTable.usuarioId] =
        this.idUsuario
    stmt[LancamentosFixosTable.tipo] =
        this.tipo.name
    stmt[LancamentosFixosTable.descricao] =
        this.descricao
    stmt[LancamentosFixosTable.valor] = this.valor
    stmt[LancamentosFixosTable.diaVencimento] =
        this.diaVencimento
    stmt[LancamentosFixosTable.mesInicio] =
        this.mesInicio.toFormatoSlug()
    stmt[LancamentosFixosTable.formaPagamento] =
        this.formaPagamento.name
    stmt[LancamentosFixosTable.cartaoId] =
        this.idCartao
    stmt[LancamentosFixosTable.categoriaId] =
        this.idCategoria
    stmt[LancamentosFixosTable.ativo] = this.ativo
    stmt[LancamentosFixosTable.criadoEm] =
        this.criadoEm
    stmt[LancamentosFixosTable.atualizadoEm] =
        this.atualizadoEm
    stmt[LancamentosFixosTable.excluidoEm] =
        this.excluidoEm?.toFormatoSlug()
}
