package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.service.LancamentosFixosService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

class LancamentosFixosController(
    private val service: LancamentosFixosService
) {
    suspend fun buscarTodos(call: ApplicationCall) {
        val lancamentos = service.buscarTodos(idUsuario).map { it.toResponse() }
        call.respond(lancamentos)
    }

    suspend fun buscarPorId(call: ApplicationCall) {
        val id = call.parameters["lancamentoFixoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val lancamento = service.buscarPorId(id, idUsuario)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(lancamento.toResponse())
    }

    suspend fun criar(call: ApplicationCall) {
        val request = call.receive<LancamentoFixoRequest>()
        val lancamento = service.criar(idUsuario, request)
        call.respond(HttpStatusCode.Created, lancamento.toResponse())
    }

    suspend fun atualizar(call: ApplicationCall) {
        val id = call.parameters["lancamentoFixoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<LancamentoFixoUpdateRequest>()
        val lancamento = service.atualizar(id, idUsuario, request)
            ?: return call.respond(HttpStatusCode.NotFound)

        call.respond(lancamento.toResponse())
    }

    suspend fun deletar(call: ApplicationCall) {
        val id = call.parameters["lancamentoFixoId"]?.toLongOrNull()
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
