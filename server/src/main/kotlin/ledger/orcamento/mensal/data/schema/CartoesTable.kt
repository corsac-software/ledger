package br.dev.brunorsch.ledger.orcamento.mensal.data.schema

import br.dev.brunorsch.ledger.orcamento.mensal.domain.cartoes.Cartao
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.datetime.datetime

object CartoesTable : LongIdTable("cartoes") {
    val usuarioId = long("usuario_id")
    val nome = varchar("nome", 16)
    val icone = varchar("icone", 16)
    val cor = char("cor", 7)
    val ativo = bool("ativo").default(true)
    val criadoEm = datetime("criado_em")
    val atualizadoEm = datetime("atualizado_em")
}

fun ResultRow.toCartao() = Cartao(
    id = this[CartoesTable.id].value,
    idUsuario = this[CartoesTable.usuarioId],
    nome = this[CartoesTable.nome],
    icone = this[CartoesTable.icone],
    cor = this[CartoesTable.cor],
    ativo = this[CartoesTable.ativo],
    criadoEm = this[CartoesTable.criadoEm],
    atualizadoEm = this[CartoesTable.atualizadoEm]
)

fun Cartao.toStatement(stmt: UpdateBuilder<*>) {
    stmt[CartoesTable.usuarioId] = this.idUsuario
    stmt[CartoesTable.nome] = this.nome
    stmt[CartoesTable.icone] = this.icone
    stmt[CartoesTable.cor] = this.cor
    stmt[CartoesTable.ativo] = this.ativo
    stmt[CartoesTable.criadoEm] = this.criadoEm
    stmt[CartoesTable.atualizadoEm] = this.atualizadoEm
}
