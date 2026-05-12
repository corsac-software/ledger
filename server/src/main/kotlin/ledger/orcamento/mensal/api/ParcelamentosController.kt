package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.ParcelamentoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.ParcelamentoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.toResponse
import br.dev.brunorsch.ledger.orcamento.mensal.service.cartoes.ParcelamentosCrudService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class ParcelamentosController(
    private val service: ParcelamentosCrudService
) {
    suspend fun buscarTodos(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val parcelamentos = service.buscarTodos(idCartao, idUsuario)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(parcelamentos.map { it.toResponse() })
    }

    suspend fun buscarPorId(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val id = call.parameters["parcelamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val parcelamento = service.buscarPorId(id, idCartao, idUsuario)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(parcelamento.toResponse())
    }

    suspend fun criar(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<ParcelamentoRequest>()
        val parcelamento = service.criar(idCartao, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.Created, parcelamento.toResponse())
    }

    suspend fun atualizar(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val id = call.parameters["parcelamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<ParcelamentoUpdateRequest>()
        val parcelamento = service.atualizar(id, idCartao, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(parcelamento.toResponse())
    }

    suspend fun deletar(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val id = call.parameters["parcelamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        return service.deletar(id, idCartao, idUsuario).fold(
            onSuccess = {
                call.respond(HttpStatusCode.NoContent)
            },
            onFailure = {
                call.respond(HttpStatusCode.NotFound)
            }
        )
    }
}
