package br.dev.brunorsch.ledger.orcamento.mensal.service.lancamentos

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.LancamentosFixosRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.LancamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.RECEITA
import br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos.TipoLancamento.valueOf
import br.dev.brunorsch.ledger.utils.slf4j

class LancamentosMensaisService(
    private val repository: LancamentosMensaisRepository,
    private val orcamentosMensaisRepository: OrcamentosMensaisRepository,
    private val lancamentosFixosRepository: LancamentosFixosRepository,
) {
    private val log = slf4j()

    fun buscarPorOrcamentoId(id: Long, idUsuario: Long): List<LancamentoMensal> {
        return repository.buscarPorOrcamentoId(id, idUsuario)
    }

    fun importarLancamentosFixos(orcamentoId: Long, idUsuario: Long): List<LancamentoMensal> {
        val orcamento = orcamentosMensaisRepository.buscarPorId(orcamentoId, idUsuario)
            ?: throw IllegalArgumentException("Orçamento não encontrado")

        return importarLancamentosFixos(orcamento)
    }

    fun importarLancamentosFixos(orcamento: OrcamentoMensal): List<LancamentoMensal> {
        val lancamentosFixos = lancamentosFixosRepository.buscarParaImportacao(orcamento.idUsuario, orcamento.anoMes)

        val lancamentos = lancamentosFixos.map { lancamentoFixo ->
            val lancamentoCriado: LancamentoMensal
            if (lancamentoFixo.tipo == RECEITA) {
                lancamentoCriado = LancamentoMensal.criarReceita(
                    orcamento = orcamento,
                    descricao = lancamentoFixo.descricao,
                    valor = lancamentoFixo.valor
                )
                orcamento.seqReceita++
            } else {
                lancamentoCriado = LancamentoMensal.criarDespesa(
                    orcamento = orcamento,
                    descricao = lancamentoFixo.descricao,
                    valor = lancamentoFixo.valor,
                )
                orcamento.seqDespesa++
            }

            return@map lancamentoCriado
        }

        val criados = repository.criarBatch(orcamento.id, lancamentos)

        if (criados.isNotEmpty()) {
            orcamentosMensaisRepository.atualizarSequencias(orcamento.id, orcamento.seqReceita, orcamento.seqDespesa)
        }

        return criados
    }

    fun criar(orcamentoId: Long, idUsuario: Long, request: LancamentoRequest): LancamentoMensal {
        log.info("Criando lançamento no orçamento ID [$orcamentoId]: ${request.tipo} - ${request.descricao}")

        val orcamento = orcamentosMensaisRepository.buscarPorId(orcamentoId, idUsuario)
            ?: throw IllegalArgumentException("Orçamento não encontrado")

        val tipo = valueOf(request.tipo.uppercase())

        val lancamento: LancamentoMensal

        if (tipo == RECEITA) {
            lancamento = LancamentoMensal.criarReceita(
                orcamento = orcamento,
                descricao = request.descricao,
                valor = request.valor
            )
            orcamento.seqReceita++
        } else {
            lancamento = LancamentoMensal.criarDespesa(
                orcamento = orcamento,
                descricao = request.descricao,
                valor = request.valor
            )
            orcamento.seqDespesa++
        }

        orcamentosMensaisRepository.atualizarSequencias(orcamento.id, orcamento.seqReceita, orcamento.seqDespesa)

        return repository.criar(orcamentoId, lancamento)
    }

    fun atualizar(
        orcamentoId: Long,
        lancamentoId: Long,
        idUsuario: Long,
        request: LancamentoUpdateRequest
    ): LancamentoMensal {
        log.info("Atualizando lançamento ID [$lancamentoId] no orçamento ID [$orcamentoId]")

        validarLancamentoExistente(orcamentoId, lancamentoId, idUsuario)

        if (request.descricao != null && request.descricao.length > 32) {
            throw IllegalArgumentException("Descrição deve ter no máximo 32 caracteres")
        }

        repository.atualizar(
            id = lancamentoId,
            descricao = request.descricao,
            valor = request.valor,
            statusDespesa = request.statusDespesa
        )

        return repository.buscarPorId(lancamentoId, orcamentoId, idUsuario)!!
    }

    fun excluir(orcamentoId: Long, lancamentoId: Long, idUsuario: Long) {
        log.info("Excluindo lançamento ID [$lancamentoId] do orçamento ID [$orcamentoId]")

        validarLancamentoExistente(orcamentoId, lancamentoId, idUsuario)

        repository.excluir(lancamentoId, orcamentoId)
    }

    private fun validarLancamentoExistente(orcamentoId: Long, lancamentoId: Long, idUsuario: Long) {
        if (!repository.existePorId(lancamentoId, orcamentoId, idUsuario)) {
            throw IllegalArgumentException("Lançamento não encontrado")
        }
    }
}
