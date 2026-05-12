package br.dev.brunorsch.ledger.orcamento.mensal.service

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoFixoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoFixoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.LancamentosFixosRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.AnoMes
import br.dev.brunorsch.ledger.orcamento.mensal.domain.LancamentoFixo
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento
import br.dev.brunorsch.ledger.orcamento.mensal.domain.TipoLancamento.valueOf
import br.dev.brunorsch.ledger.utils.now
import kotlinx.datetime.LocalDateTime
import java.time.YearMonth

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
        val mesInicio = AnoMes.parse(request.mesInicio)
        val formaPagamento = parseFormaPagamento(request.formaPagamento)
        validarDescricao(request.descricao)
        validarDiaVencimento(request.diaVencimento)
        validarFormaPagamento(formaPagamento, request.idCartao)
        validarCategoria(request.idCategoria, idUsuario)

        return repository.criar(
            LancamentoFixo(
                idUsuario = idUsuario,
                tipo = tipo,
                descricao = request.descricao,
                valor = request.valor,
                diaVencimento = request.diaVencimento,
                mesInicio = mesInicio,
                formaPagamento = formaPagamento,
                idCartao = request.idCartao,
                idCategoria = request.idCategoria,
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
                mesInicio = request.mesInicio?.let { AnoMes.parse(it) } ?: existente.mesInicio,
                formaPagamento = formaPagamento,
                idCartao = idCartao,
                idCategoria = idCategoria,
                atualizadoEm = LocalDateTime.now(),
            )
        )
    }

    fun deletar(id: Long, idUsuario: Long): Result<Unit> {
        val agora = LocalDateTime.now()
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

    private fun YearMonth.toAnoMes() = AnoMes(year, monthValue)
}
