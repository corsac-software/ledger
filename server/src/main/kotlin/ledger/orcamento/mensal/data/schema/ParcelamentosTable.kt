package br.dev.brunorsch.ledger.orcamento.mensal.data.schema

import br.dev.brunorsch.ledger.orcamento.mensal.domain.Parcelamento
import br.dev.brunorsch.ledger.orcamento.mensal.domain.toAnoMes
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.datetime.datetime

object ParcelamentosTable : LongIdTable("parcelamentos") {
    val cartaoId = long("cartao_id")
        .references(CartoesTable.id)
    val nome = varchar("nome", 32)
    val valor = decimal("valor", 10, 2)
    val parcelas = integer("parcelas")
    val mesInicio = varchar("mes_inicio", 6)
    val ativo = bool("ativo").default(true)
    val criadoEm = datetime("criado_em")
    val atualizadoEm = datetime("atualizado_em")
    val excluidoEm = datetime("excluido_em").nullable()
}

fun ResultRow.toParcelamento() = Parcelamento(
    id = this[ParcelamentosTable.id].value,
    idCartao = this[ParcelamentosTable.cartaoId],
    nome = this[ParcelamentosTable.nome],
    valor = this[ParcelamentosTable.valor],
    parcelas = this[ParcelamentosTable.parcelas],
    mesInicio = this[ParcelamentosTable.mesInicio].toAnoMes(),
    ativo = this[ParcelamentosTable.ativo],
    criadoEm = this[ParcelamentosTable.criadoEm],
    atualizadoEm = this[ParcelamentosTable.atualizadoEm],
    excluidoEm = this[ParcelamentosTable.excluidoEm]
)

fun Parcelamento.toStatement(stmt: UpdateBuilder<*>) {
    stmt[ParcelamentosTable.cartaoId] = this.idCartao
    stmt[ParcelamentosTable.nome] = this.nome
    stmt[ParcelamentosTable.valor] = this.valor
    stmt[ParcelamentosTable.parcelas] = this.parcelas
    stmt[ParcelamentosTable.mesInicio] = this.mesInicio.toFormatoSlug()
    stmt[ParcelamentosTable.ativo] = this.ativo
    stmt[ParcelamentosTable.criadoEm] = this.criadoEm
    stmt[ParcelamentosTable.atualizadoEm] = this.atualizadoEm
    stmt[ParcelamentosTable.excluidoEm] = this.excluidoEm
}
