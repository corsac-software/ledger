package br.dev.brunorsch.ledger.orcamento.mensal.service.lancamentos

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CategoriaRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CategoriaUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.CategoriasRepository
import br.dev.brunorsch.ledger.orcamento.mensal.domain.lancamentos.Categoria
import br.dev.brunorsch.ledger.utils.idNaoInserido

class CategoriasCrudService(
    private val repository: CategoriasRepository
) {
    fun buscarTodas(idUsuario: Long): List<Categoria> {
        return repository.buscarTodas(idUsuario)
    }

    fun criar(idUsuario: Long, request: CategoriaRequest): Categoria {
        validarNome(request.nome)
        validarIcone(request.icone)

        return repository.criar(
            Categoria(
                idUsuario = idUsuario,
                nome = request.nome,
                icone = request.icone,
            )
        )
    }

    fun atualizar(id: Long, idUsuario: Long, request: CategoriaUpdateRequest): Categoria? {
        request.nome?.let { validarNome(it) }
        request.icone?.let { validarIcone(it) }

        return repository.atualizar(id, idUsuario, request)
    }

    fun deletar(id: Long, idUsuario: Long): Result<Unit> {
        return repository.deletar(id, idUsuario)
    }

    fun setupCategoriasPadrao(idUsuario: Long): Result<Unit> {
        if (repository.existeAlguma(idUsuario))
            return Result.failure(IllegalStateException("O usuário já possui categoria cadastrada"))

        repository.criarTodas(
            categoriasPadrao.map { (icone, nome) ->
                Categoria(
                    id = idNaoInserido,
                    idUsuario = idUsuario,
                    nome = nome,
                    icone = icone,
                    ativo = true
                )
            }
        )

        return Result.success(Unit)
    }

    private fun validarNome(nome: String) {
        require(nome.isNotBlank()) { "Nome da categoria é obrigatório" }
        require(nome.length <= 16) { "Nome da categoria deve ter no máximo 16 caracteres" }
    }

    private fun validarIcone(icone: String) {
        require(icone.isNotBlank()) { "Ícone da categoria é obrigatório" }
        require(icone.length <= 16) { "Ícone da categoria deve ter no máximo 16 caracteres" }
    }

    companion object {
        private val categoriasPadrao = listOf(
            "🏠" to "CASA",
            "📱" to "TELEFONE",
            "🏠" to "ALUGUEL",
            "🎬" to "STREAMING",
            "🔒" to "SEGURO",
            "📈" to "INVESTIMENTO",
            "📦" to "OUTRO"
        )
    }
}