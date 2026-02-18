package br.dev.brunorsch.ledger.orcamento.mensal.service

import br.dev.brunorsch.ledger.orcamento.mensal.api.OrcamentoMensalRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoMensal
import br.dev.brunorsch.ledger.orcamento.mensal.domain.OrcamentoMensal
import br.dev.brunorsch.utils.idNaoInserido
import br.dev.brunorsch.utils.slf4j

class OrcamentosMensaisService(
    private val repository: OrcamentosMensaisRepository,
) {
    private val log = slf4j()

    fun criar(request: OrcamentoMensalRequest): OrcamentoMensal {
        log.info("Criando novo orçamento mensal para usuario ID " +
                "[${request.idUsuario}]: ${request.ano}-${request.mes}")

        val orcamento = OrcamentoMensal(
            id = idNaoInserido,
            idUsuario = request.idUsuario,
            anoMes = AnoMes(request.ano, request.mes),
            dataInicio = request.dataInicio,
            dataFim = request.dataFim
        )

        return repository.criar(orcamento)
    }

    fun buscarPorId(id: Long, idUsuario: Long): OrcamentoMensal? {
        return repository.buscarPorId(id, idUsuario)
    }

    fun buscarLancamentosPorId(id: Long, idUsuario: Long): List<LancamentoMensal> {
        return repository.buscarLancamentosPorId(id, idUsuario)
    }

    fun excluir(id: Long, idUsuario: Long) {
        log.info("Excluindo orçamento mensal ID [$id] para usuario ID [$idUsuario]")
        repository.excluir(id, idUsuario)
    }
}
