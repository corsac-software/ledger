package br.dev.brunorsch.ledger.orcamento.mensal.service

import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoFixoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoFixoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.LancamentosFixosRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoFixo
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.valueOf
import br.dev.brunorsch.ledger.orcamento.mensal.domain.toAnoMes
import br.dev.brunorsch.ledger.utils.idNaoInserido
import java.time.YearMonth
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LancamentosFixosService(
    private val repository: LancamentosFixosRepository
) {
    fun buscarTodos(idUsuario: Long): List<LancamentoFixo> {
        return repository.buscarTodos(idUsuario)
    }

    fun buscarPorId(id: Long, idUsuario: Long): LancamentoFixo? {
        return repository.buscarPorId(id, idUsuario)
    }

    fun criar(idUsuario: Long, request: LancamentoFixoRequest): LancamentoFixo {
        val tipo = parseTipo(request.tipo)
        val mesInicio = parseAnoMes(request.mesInicio)
        val formaPagamento = parseFormaPagamento(request.formaPagamento)
        validarDescricao(request.descricao)
        validarDiaVencimento(request.diaVencimento)
        validarFormaPagamento(formaPagamento, request.idCartao)
        validarCategoria(request.idCategoria, idUsuario)

        val agora = Clock.System.now()
        return repository.criar(
            LancamentoFixo(
                id = idNaoInserido,
                idUsuario = idUsuario,
                tipo = tipo,
                descricao = request.descricao,
                valor = request.valor,
                diaVencimento = request.diaVencimento,
                mesInicio = mesInicio,
                formaPagamento = formaPagamento,
                idCartao = request.idCartao,
                idCategoria = request.idCategoria,
                ativo = true,
                criadoEm = agora,
                atualizadoEm = agora,
                excluidoEm = null
            )
        )
    }

    fun atualizar(id: Long, idUsuario: Long, request: LancamentoFixoUpdateRequest): LancamentoFixo? {
        val existente = repository.buscarPorId(id, idUsuario)
            ?: return null

        val formaPagamento = request.formaPagamento
            ?.let { parseFormaPagamento(it) }
            ?: existente.formaPagamento
        val idCartao = when {
            formaPagamento != LancamentoFixo.FormaPagamento.CARTAO -> null
            request.idCartao != null -> request.idCartao
            else -> existente.idCartao
        }
        val idCategoria = request.idCategoria ?: existente.idCategoria

        request.descricao?.let { validarDescricao(it) }
        request.diaVencimento?.let { validarDiaVencimento(it) }
        validarFormaPagamento(formaPagamento, idCartao)
        if (request.idCategoria != null) validarCategoria(idCategoria, idUsuario)

        return repository.atualizar(
            existente.copy(
                tipo = request.tipo?.let { parseTipo(it) } ?: existente.tipo,
                descricao = request.descricao ?: existente.descricao,
                valor = request.valor ?: existente.valor,
                diaVencimento = request.diaVencimento ?: existente.diaVencimento,
                mesInicio = request.mesInicio?.let { parseAnoMes(it) } ?: existente.mesInicio,
                formaPagamento = formaPagamento,
                idCartao = idCartao,
                idCategoria = idCategoria,
                ativo = request.ativo ?: existente.ativo,
                atualizadoEm = Clock.System.now(),
                excluidoEm = if (request.ativo == true) null else existente.excluidoEm
            )
        )
    }

    fun deletar(id: Long, idUsuario: Long): Result<Unit> {
        val agora = Clock.System.now()
        return repository.deletar(
            id = id,
            idUsuario = idUsuario,
            excluidoEm = YearMonth.now().toAnoMes(),
            atualizadoEm = agora
        )
    }

    private fun validarDescricao(descricao: String) {
        require(descricao.length <= 32) { "Descrição deve ter no máximo 32 caracteres" }
    }

    private fun validarDiaVencimento(diaVencimento: Int) {
        require(diaVencimento in 1..31) { "Dia de vencimento deve estar entre 1 e 31" }
    }

    private fun validarCategoria(idCategoria: Long, idUsuario: Long) {
        require(repository.categoriaExiste(idCategoria, idUsuario)) { "Categoria não encontrada" }
    }

    private fun validarFormaPagamento(formaPagamento: LancamentoFixo.FormaPagamento, idCartao: Long?) {
        if (formaPagamento == LancamentoFixo.FormaPagamento.CARTAO) {
            require(idCartao != null) { "Cartão é obrigatório para forma de pagamento CARTAO" }
        } else {
            require(idCartao == null) { "Cartão só pode ser informado para forma de pagamento CARTAO" }
        }
    }

    private fun parseTipo(tipo: String): TipoLancamento {
        return valueOf(tipo.uppercase())
    }

    private fun parseFormaPagamento(formaPagamento: String): LancamentoFixo.FormaPagamento {
        return LancamentoFixo.FormaPagamento.valueOf(formaPagamento.uppercase())
    }

    private fun parseAnoMes(anoMes: String): AnoMes {
        return anoMes.replace("-", "").toAnoMes()
    }

    private fun YearMonth.toAnoMes() = AnoMes(year, monthValue)
}
