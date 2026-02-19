package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.service.OrcamentosMensaisService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

// TODO: Dinamizar com sistema de autenticação
const val idUsuario: Long = 1

class OrcamentosMensaisController(
    private val service: OrcamentosMensaisService
) {
    suspend fun criar(call: ApplicationCall) {
        val request = call.receive<OrcamentoMensalRequest>()
        val criado = service.criar(request)
        call.respond(HttpStatusCode.Created, criado.toResponse())
    }

    suspend fun buscarPorId(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val orcamento = service.buscarPorId(id, idUsuario)

        if (orcamento == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(orcamento.toResponse())
        }
    }

    suspend fun buscarLancamentosPorId(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val lancamentos = service.buscarLancamentosPorId(id, idUsuario)
            .map { it.toResponse() }

        call.respond(lancamentos)
    }

    suspend fun excluir(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        service.excluir(id, idUsuario)

        call.respond(HttpStatusCode.NoContent)
    }
}
