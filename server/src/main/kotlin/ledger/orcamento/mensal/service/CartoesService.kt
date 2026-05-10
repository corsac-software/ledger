package br.dev.brunorsch.ledger.orcamento.mensal.service

import br.dev.brunorsch.ledger.orcamento.mensal.api.CartaoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.CartaoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.CartoesRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.Cartao
import br.dev.brunorsch.ledger.utils.idNaoInserido
import br.dev.brunorsch.ledger.utils.now
import kotlinx.datetime.LocalDateTime

class CartoesService(
    private val repository: CartoesRepository
) {
    fun buscarTodos(idUsuario: Long): List<Cartao> {
        return repository.buscarTodos(idUsuario)
    }

    fun buscarPorId(id: Long, idUsuario: Long): Cartao? {
        return repository.buscarPorId(id, idUsuario)
    }

    fun criar(idUsuario: Long, request: CartaoRequest): Cartao {
        validarNome(request.nome)
        validarIcone(request.icone)
        validarCor(request.cor)

        val agora = LocalDateTime.now()
        return repository.criar(
            Cartao(
                id = idNaoInserido,
                idUsuario = idUsuario,
                nome = request.nome,
                icone = request.icone,
                cor = request.cor,
                ativo = true,
                criadoEm = agora,
                atualizadoEm = agora
            )
        )
    }

    fun atualizar(id: Long, idUsuario: Long, request: CartaoUpdateRequest): Cartao? {
        val existente = repository.buscarPorId(id, idUsuario)
            ?: return null

        request.nome?.let { validarNome(it) }
        request.icone?.let { validarIcone(it) }
        request.cor?.let { validarCor(it) }

        return repository.atualizar(
            existente.copy(
                nome = request.nome ?: existente.nome,
                icone = request.icone ?: existente.icone,
                cor = request.cor ?: existente.cor,
                ativo = request.ativo ?: existente.ativo,
                atualizadoEm = LocalDateTime.now()
            )
        )
    }

    fun deletar(id: Long, idUsuario: Long): Result<Unit> {
        return repository.deletar(id, idUsuario)
    }

    private fun validarNome(nome: String) {
        require(nome.isNotBlank()) { "Nome do cartão é obrigatório" }
        require(nome.length <= 16) { "Nome do cartão deve ter no máximo 16 caracteres" }
    }

    private fun validarIcone(icone: String) {
        require(icone.isNotBlank()) { "Ícone do cartão é obrigatório" }
        require(icone.length <= 16) { "Ícone do cartão deve ter no máximo 16 caracteres" }
    }

    private fun validarCor(cor: String) {
        require(cor.matches(Regex("^#[0-9A-Fa-f]{6}$"))) { "Cor do cartão deve estar no formato hexadecimal #RRGGBB" }
    }
}
