package br.dev.corsac.ledger.orcamento.mensal.service.cartoes

import br.dev.corsac.ledger.orcamento.mensal.api.dtos.ParcelamentoRequest
import br.dev.corsac.ledger.orcamento.mensal.api.dtos.ParcelamentoUpdateRequest
import br.dev.corsac.ledger.orcamento.mensal.data.repository.ParcelamentosRepository
import br.dev.corsac.ledger.orcamento.mensal.domain.AnoMes
import br.dev.corsac.ledger.orcamento.mensal.domain.cartoes.Parcelamento
import br.dev.corsac.ledger.utils.now
import kotlinx.datetime.LocalDateTime

class ParcelamentosCrudService(
    private val repository: ParcelamentosRepository
) {
    fun buscarTodos(idCartao: Long, idUsuario: Long): List<Parcelamento>? {
        if (!repository.cartaoExiste(idCartao, idUsuario)) return null
        return repository.buscarTodos(idCartao, idUsuario)
    }

    fun buscarPorId(id: Long, idCartao: Long, idUsuario: Long): Parcelamento? {
        return repository.buscarPorId(id, idCartao, idUsuario)
    }

    fun criar(idCartao: Long, idUsuario: Long, request: ParcelamentoRequest): Parcelamento? {
        if (!repository.cartaoExiste(idCartao, idUsuario)) return null

        validarNome(request.nome)
        validarParcelas(request.parcelas)
        val mesInicio = AnoMes.parse(request.mesInicio)

        return repository.criar(
            Parcelamento(
                idCartao = idCartao,
                nome = request.nome,
                valor = request.valor,
                parcelas = request.parcelas,
                mesInicio = mesInicio,
            )
        )
    }

    fun atualizar(id: Long, idCartao: Long, idUsuario: Long, request: ParcelamentoUpdateRequest): Parcelamento? {
        val existente = repository.buscarPorId(id, idCartao, idUsuario)
            ?: return null

        request.nome?.let { validarNome(it) }
        request.parcelas?.let { validarParcelas(it) }

        return repository.atualizar(
            existente.copy(
                nome = request.nome ?: existente.nome,
                valor = request.valor ?: existente.valor,
                parcelas = request.parcelas ?: existente.parcelas,
                mesInicio = request.mesInicio?.let { AnoMes.parse(it) } ?: existente.mesInicio,
                atualizadoEm = LocalDateTime.now(),
            ),
            idUsuario
        )
    }

    fun deletar(id: Long, idCartao: Long, idUsuario: Long): Result<Unit> {
        return repository.deletar(
            id = id,
            idCartao = idCartao,
            idUsuario = idUsuario
        )
    }

    private fun validarNome(nome: String) {
        require(nome.isNotBlank()) { "Nome do parcelamento é obrigatório" }
        require(nome.length <= 32) { "Nome do parcelamento deve ter no máximo 32 caracteres" }
    }

    private fun validarParcelas(parcelas: Int) {
        require(parcelas > 0) { "Parcelas deve ser maior que zero" }
    }
}