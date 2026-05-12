package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CategoriaRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CategoriaUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.toResponse
import br.dev.brunorsch.ledger.orcamento.mensal.service.CategoriasService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class CategoriasController(
    private val service: CategoriasService
) {
    suspend fun buscarTodas(call: ApplicationCall) {
        val categorias = service.buscarTodas(idUsuario).map { it.toResponse() }
        call.respond(categorias)
    }

    suspend fun criar(call: ApplicationCall) {
        val request = call.receive<CategoriaRequest>()
        val categoria = service.criar(idUsuario, request)
        call.respond(HttpStatusCode.Created, categoria.toResponse())
    }

    suspend fun atualizar(call: ApplicationCall) {
        val id = call.parameters["categoriaId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<CategoriaUpdateRequest>()
        val categoria = service.atualizar(id, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(categoria.toResponse())
    }

    suspend fun deletar(call: ApplicationCall) {
        val id = call.parameters["categoriaId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        return service.deletar(id, idUsuario).fold(
            onSuccess = {
                call.respond(HttpStatusCode.NoContent)
            },
            onFailure = {
                call.respond(HttpStatusCode.NotFound)
            }
        )
    }

    suspend fun setupCategoriasPadrao(call: ApplicationCall) {
        return service.setupCategoriasPadrao(idUsuario).fold(
            onSuccess = {
                call.respond(HttpStatusCode.Created)
            },
            onFailure = { error ->
                if (error is IllegalStateException) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        )
    }
}
