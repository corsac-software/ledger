package br.dev.corsac.ledger.orcamento.mensal.service

import br.dev.corsac.ledger.orcamento.mensal.api.dtos.OrcamentoMensalRequest
import br.dev.corsac.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.corsac.ledger.orcamento.mensal.domain.OrcamentoMensal
import br.dev.corsac.ledger.orcamento.mensal.domain.toAnoMes
import br.dev.corsac.ledger.orcamento.mensal.service.lancamentos.LancamentosMensaisService
import br.dev.corsac.ledger.utils.idNaoInserido
import br.dev.corsac.ledger.utils.slf4j
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import java.time.YearMonth

class OrcamentosMensaisService(
    private val repository: OrcamentosMensaisRepository,
    private val lancamentosMensaisService: LancamentosMensaisService,
) {
    private val log = slf4j()

    fun criar(request: OrcamentoMensalRequest): OrcamentoMensal {
        val anoMes = request.anoMes.toAnoMes()
        val ano = anoMes.ano.value
        val mes = anoMes.mes.number
        val dataInicio = LocalDate(ano, mes, 1)
        val dataFim = LocalDate(ano, mes, YearMonth.of(ano, mes).lengthOfMonth())

        log.info(
            "Criando novo orçamento mensal para usuario ID " +
                    "[${request.idUsuario}]: $ano-$mes"
        )

        val orcamento = OrcamentoMensal(
            id = idNaoInserido,
            idUsuario = request.idUsuario,
            anoMes = anoMes,
            dataInicio = dataInicio,
            dataFim = dataFim
        )

        val criado = repository.criar(orcamento)
        lancamentosMensaisService.importarLancamentosFixos(criado)

        return criado
    }

    fun buscarTodos(idUsuario: Long): List<OrcamentoMensal> {
        return repository.buscarTodos(idUsuario)
    }

    fun buscarPorId(id: Long, idUsuario: Long): OrcamentoMensal? {
        return repository.buscarPorId(id, idUsuario)
    }

    fun excluir(id: Long, idUsuario: Long) {
        log.info("Excluindo orçamento mensal ID [$id] para usuario ID [$idUsuario]")

        validarOrcamentoExistente(id, idUsuario)

        repository.excluir(id, idUsuario)
    }

    private fun validarOrcamentoExistente(id: Long, idUsuario: Long) {
        if (!repository.existe(id, idUsuario)) {
            throw IllegalArgumentException("Orçamento mensal não encontrado")
        }
    }
}
