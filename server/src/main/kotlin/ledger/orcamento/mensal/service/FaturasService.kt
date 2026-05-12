package br.dev.brunorsch.ledger.orcamento.mensal.service

import br.dev.brunorsch.ledger.orcamento.mensal.api.FaturaRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.FaturaUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.FaturasRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.Fatura
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.toAnoMes
import br.dev.brunorsch.ledger.utils.idNaoInserido
import br.dev.brunorsch.ledger.utils.now
import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

class FaturasService(
    private val repository: FaturasRepository,
    private val orcamentosMensaisRepository: OrcamentosMensaisRepository
) {
    fun buscarTodos(idCartao: Long, idUsuario: Long): List<Fatura>? {
        if (!repository.cartaoExiste(idCartao, idUsuario)) return null
        return repository.buscarTodos(idCartao, idUsuario)
    }

    fun buscarPorId(id: Long, idCartao: Long, idUsuario: Long): Fatura? {
        return repository.buscarPorId(id, idCartao, idUsuario)
    }

    fun criar(idCartao: Long, idUsuario: Long, request: FaturaRequest): Fatura? {
        if (!repository.cartaoExiste(idCartao, idUsuario)) return null

        val orcamento = orcamentosMensaisRepository.buscarPorId(request.orcamentoId, idUsuario)
            ?: throw IllegalArgumentException("Orçamento mensal não encontrado")

        val mes = AnoMes.parse(request.mes)

        validarDescricao(request.descricao)

        val lancamento = LancamentoMensal.criarDespesa(
            orcamento = orcamento,
            descricao = request.descricao,
            valor = request.valor
        )

        orcamentosMensaisRepository.incrementarSeqDespesa(request.orcamentoId)

        val lancamentoCriado = orcamentosMensaisRepository.criarLancamento(request.orcamentoId, lancamento)

        val fatura = repository.criar(
            Fatura(
                idCartao = idCartao,
                idLancamento = lancamentoCriado.id,
                valor = request.valor,
                mes = mes,
            )
        )

        repository.atualizarFaturaIdLancamento(lancamentoCriado.id, fatura.id)

        return fatura
    }

    fun atualizar(id: Long, idCartao: Long, idUsuario: Long, request: FaturaUpdateRequest): Fatura? {
        val existente = repository.buscarPorId(id, idCartao, idUsuario)
            ?: return null

        request.descricao?.let { validarDescricao(it) }

        if (request.descricao != null || request.valor != null) {
            orcamentosMensaisRepository.atualizarLancamento(
                id = existente.idLancamento,
                descricao = request.descricao,
                valor = request.valor,
                statusDespesa = null
            )
        }

        return repository.atualizar(
            existente.copy(
                valor = request.valor ?: existente.valor,
                mes = request.mes?.let { AnoMes.parse(it) } ?: existente.mes,
                atualizadoEm = LocalDateTime.now()
            ),
            idUsuario
        )
    }

    fun deletar(id: Long, idCartao: Long, idUsuario: Long): Result<Unit> {
        return repository.deletar(id, idCartao, idUsuario)
    }

    private fun validarDescricao(descricao: String) {
        require(descricao.isNotBlank()) { "Descrição da fatura é obrigatória" }
        require(descricao.length <= 32) { "Descrição deve ter no máximo 32 caracteres" }
    }
}