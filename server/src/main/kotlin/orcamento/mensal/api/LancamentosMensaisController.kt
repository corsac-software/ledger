package br.dev.corsac.ledger.orcamento.mensal.api

import br.dev.corsac.ledger.orcamento.mensal.api.dtos.LancamentoRequest
import br.dev.corsac.ledger.orcamento.mensal.api.dtos.LancamentoUpdateRequest
import br.dev.corsac.ledger.orcamento.mensal.api.dtos.toResponse
import br.dev.corsac.ledger.orcamento.mensal.service.lancamentos.LancamentosMensaisService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class LancamentosMensaisController(
    private val service: LancamentosMensaisService
) {
    suspend fun buscarPorOrcamentoId(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val lancamentos = service.buscarPorOrcamentoId(id, idUsuario)
            .map { it.toResponse() }

        call.respond(lancamentos)
    }

    suspend fun importarLancamentosFixos(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val lancamentos = service.importarLancamentosFixos(id, idUsuario)
            .map { it.toResponse() }

        call.respond(HttpStatusCode.Created, lancamentos)
    }

    suspend fun criar(call: ApplicationCall) {
        val orcamentoId = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<LancamentoRequest>()
        val criado = service.criar(orcamentoId, idUsuario, request)
        call.respond(HttpStatusCode.Created, criado.toResponse())
    }

    suspend fun atualizar(call: ApplicationCall) {
        val orcamentoId = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val lancamentoId = call.parameters["lancamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<LancamentoUpdateRequest>()
        val atualizado = service.atualizar(
            orcamentoId, lancamentoId,
            idUsuario, request
        )
        call.respond(atualizado.toResponse())
    }

    suspend fun excluir(call: ApplicationCall) {
        val orcamentoId = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val lancamentoId = call.parameters["lancamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        service.excluir(orcamentoId, lancamentoId, idUsuario)
        call.respond(HttpStatusCode.NoContent)
    }
}
