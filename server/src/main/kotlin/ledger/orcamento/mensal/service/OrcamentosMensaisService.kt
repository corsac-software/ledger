package br.dev.brunorsch.ledger.orcamento.mensal.service

import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.OrcamentoMensalRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.DESPESA
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.RECEITA
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.valueOf
import br.dev.brunorsch.ledger.orcamento.mensal.domain.toAnoMes
import br.dev.brunorsch.ledger.utils.idNaoInserido
import br.dev.brunorsch.ledger.utils.slf4j
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.time.YearMonth

class OrcamentosMensaisService(
    private val repository: OrcamentosMensaisRepository,
) {
    private val log = slf4j()

    fun criar(request: OrcamentoMensalRequest): OrcamentoMensal {
        val anoMes = request.anoMes.toAnoMes()
        val ano = anoMes.ano.value
        val mes = anoMes.mes.number
        val dataInicio = LocalDate(ano, mes, 1)
        val dataFim = LocalDate(ano, mes, YearMonth.of(ano, mes).lengthOfMonth())

        log.info("Criando novo orçamento mensal para usuario ID " +
                "[${request.idUsuario}]: $ano-$mes")

        val orcamento = OrcamentoMensal(
            id = idNaoInserido,
            idUsuario = request.idUsuario,
            anoMes = anoMes,
            dataInicio = dataInicio,
            dataFim = dataFim
        )

        val criado = repository.criar(orcamento)
        importarLancamentosFixos(criado)

        return criado
    }

    fun buscarTodos(idUsuario: Long): List<OrcamentoMensal> {
        return repository.buscarTodos(idUsuario)
    }

    fun buscarPorId(id: Long, idUsuario: Long): OrcamentoMensal? {
        return repository.buscarPorId(id, idUsuario)
    }

    fun buscarLancamentosPorId(id: Long, idUsuario: Long): List<LancamentoMensal> {
        return repository.buscarLancamentosPorId(id, idUsuario)
    }

    fun importarLancamentosFixos(orcamentoId: Long, idUsuario: Long): List<LancamentoMensal> {
        val orcamento = repository.buscarPorId(orcamentoId, idUsuario)
            ?: throw IllegalArgumentException("Orçamento não encontrado")
        return importarLancamentosFixos(orcamento)
    }

    private fun importarLancamentosFixos(orcamento: OrcamentoMensal): List<LancamentoMensal> {
        val lancamentosFixos = repository.buscarLancamentosFixosParaImportacao(orcamento.idUsuario, orcamento.anoMes)

        var seqReceita = orcamento.seqReceita
        var seqDespesa = orcamento.seqDespesa
        val lancamentos = lancamentosFixos.map { lancamentoFixo ->
            val seq = when (lancamentoFixo.tipo) {
                RECEITA -> ++seqReceita
                DESPESA -> ++seqDespesa
            }
            val slug = "${lancamentoFixo.tipo.prefixoSlug}-${orcamento.anoMes.anoAsString()}-${orcamento.anoMes.mesAsString()}-$seq"

            if (lancamentoFixo.tipo == RECEITA) {
                LancamentoMensal.criarReceita(
                    id = idNaoInserido,
                    slug = slug,
                    descricao = lancamentoFixo.descricao,
                    valor = lancamentoFixo.valor
                )
            } else {
                LancamentoMensal.criarDespesa(
                    id = idNaoInserido,
                    slug = slug,
                    descricao = lancamentoFixo.descricao,
                    valor = lancamentoFixo.valor,
                    statusDespesa = LancamentoMensal.StatusDespesa.ABERTO
                )
            }
        }

        val criados = repository.criarLancamentosBatch(orcamento.id, lancamentos)
        if (criados.isNotEmpty()) {
            repository.atualizarSequencias(orcamento.id, seqReceita, seqDespesa)
        }

        return criados
    }

    fun excluir(id: Long, idUsuario: Long) {
        log.info("Excluindo orçamento mensal ID [$id] para usuario ID [$idUsuario]")

        validarOrcamentoExistente(id, idUsuario)

        repository.excluir(id, idUsuario)
    }

    fun criarLancamento(orcamentoId: Long, idUsuario: Long, request: LancamentoRequest): LancamentoMensal {
        log.info("Criando lançamento no orçamento ID [$orcamentoId]: ${request.tipo} - ${request.descricao}")

        val orcamento = repository.buscarPorId(orcamentoId, idUsuario)
            ?: throw IllegalArgumentException("Orçamento não encontrado")

        val tipo = valueOf(request.tipo.uppercase())
        val statusDespesa = request.statusDespesa?.let {
            LancamentoMensal.StatusDespesa.valueOf(it.uppercase())
        }

        if (tipo == DESPESA && statusDespesa == null) {
            throw IllegalArgumentException("Despesa precisa de status")
        }
        if (tipo == RECEITA && statusDespesa != null) {
            throw IllegalArgumentException("Receita não pode ter status")
        }

        val seq = when (tipo) {
            RECEITA -> repository.incrementarSeqReceita(orcamentoId)
            DESPESA -> repository.incrementarSeqDespesa(orcamentoId)
        }
        val slug = "${tipo.prefixoSlug}-${orcamento.anoMes.anoAsString()}-${orcamento.anoMes.mesAsString()}-$seq"

        val lancamento = if (tipo == RECEITA) {
            LancamentoMensal.criarReceita(
                id = idNaoInserido,
                slug = slug,
                descricao = request.descricao,
                valor = request.valor
            )
        } else {
            LancamentoMensal.criarDespesa(
                id = idNaoInserido,
                slug = slug,
                descricao = request.descricao,
                valor = request.valor,
                statusDespesa = statusDespesa!!
            )
        }

        return repository.criarLancamento(orcamentoId, lancamento)
    }

    fun atualizarLancamento(
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

        repository.atualizarLancamento(
            id = lancamentoId,
            descricao = request.descricao,
            valor = request.valor,
            statusDespesa = request.statusDespesa
        )

        return repository.buscarLancamentoPorId(lancamentoId, orcamentoId, idUsuario)!!
    }

    fun excluirLancamento(orcamentoId: Long, lancamentoId: Long, idUsuario: Long) {
        log.info("Excluindo lançamento ID [$lancamentoId] do orçamento ID [$orcamentoId]")

        validarLancamentoExistente(orcamentoId, lancamentoId, idUsuario)

        repository.excluirLancamento(lancamentoId, orcamentoId)
    }

    private fun validarOrcamentoExistente(id: Long, idUsuario: Long) {
        if(!repository.existe(id, idUsuario)) {
            throw IllegalArgumentException("Orçamento mensal não encontrado")
        }
    }

    private fun validarLancamentoExistente(orcamentoId: Long, lancamentoId: Long, idUsuario: Long) {
        if(!repository.existeLancamentoPorId(lancamentoId, orcamentoId, idUsuario)) {
            throw IllegalArgumentException("Lançamento não encontrado")
        }
    }
}
