package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.FaturaRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.FaturaUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.toResponse
import br.dev.brunorsch.ledger.orcamento.mensal.service.FaturasService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class FaturasController(
    private val service: FaturasService
) {
    suspend fun buscarTodos(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val faturas = service.buscarTodos(idCartao, idUsuario)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(faturas.map { it.toResponse() })
    }

    suspend fun buscarPorId(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val id = call.parameters["faturaId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val fatura = service.buscarPorId(id, idCartao, idUsuario)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(fatura.toResponse())
    }

    suspend fun criar(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<FaturaRequest>()
        val fatura = service.criar(idCartao, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.Created, fatura.toResponse())
    }

    suspend fun atualizar(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val id = call.parameters["faturaId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<FaturaUpdateRequest>()
        val fatura = service.atualizar(id, idCartao, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(fatura.toResponse())
    }

    suspend fun deletar(call: ApplicationCall) {
        val idCartao = call.parameters["cartaoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val id = call.parameters["faturaId"]?.toLongOrNull()
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