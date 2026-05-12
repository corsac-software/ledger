package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CartaoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CartaoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.toResponse
import br.dev.brunorsch.ledger.orcamento.mensal.service.cartoes.CartoesCrudService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class CartoesController(
    private val service: CartoesCrudService
) {
    suspend fun buscarTodos(call: ApplicationCall) {
        val cartoes = service.buscarTodos(idUsuario).map { it.toResponse() }
        call.respond(cartoes)
    }

    suspend fun buscarPorId(call: ApplicationCall) {
        val id = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val cartao = service.buscarPorId(id, idUsuario)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(cartao.toResponse())
    }

    suspend fun criar(call: ApplicationCall) {
        val request = call.receive<CartaoRequest>()
        val cartao = service.criar(idUsuario, request)
        call.respond(HttpStatusCode.Created, cartao.toResponse())
    }

    suspend fun atualizar(call: ApplicationCall) {
        val id = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<CartaoUpdateRequest>()
        val cartao = service.atualizar(id, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(cartao.toResponse())
    }

    suspend fun deletar(call: ApplicationCall) {
        val id = call.parameters["cartaoId"]?.toLongOrNull()
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
}
