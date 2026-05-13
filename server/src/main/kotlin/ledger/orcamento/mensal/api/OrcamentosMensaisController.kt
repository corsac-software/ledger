package br.dev.brunorsch.ledger.orcamento.mensal.api

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.OrcamentoMensalRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.toResponse
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
    suspend fun buscarTodos(call: ApplicationCall) {
        val orcamentos = service.buscarTodos(idUsuario).map { it.toResponse() }
        call.respond(orcamentos)
    }

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

    suspend fun importarLancamentosFixos(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val lancamentos = service.importarLancamentosFixos(id, idUsuario)
            .map { it.toResponse() }

        call.respond(HttpStatusCode.Created, lancamentos)
    }

    suspend fun excluir(call: ApplicationCall) {
        val id = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        service.excluir(id, idUsuario)

        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun criarLancamento(call: ApplicationCall) {
        val orcamentoId = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<LancamentoRequest>()
        val criado = service.criarLancamento(orcamentoId, idUsuario, request)
        call.respond(HttpStatusCode.Created, criado.toResponse())
    }

    suspend fun atualizarLancamento(call: ApplicationCall) {
        val orcamentoId = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val lancamentoId = call.parameters["lancamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        val request = call.receive<LancamentoUpdateRequest>()
        val atualizado = service.atualizarLancamento(orcamentoId, lancamentoId, idUsuario, request)
        call.respond(atualizado.toResponse())
    }

    suspend fun excluirLancamento(call: ApplicationCall) {
        val orcamentoId = call.parameters["id"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)
        val lancamentoId = call.parameters["lancamentoId"]?.toLongOrNull()
            ?: return call.respond(HttpStatusCode.BadRequest)

        service.excluirLancamento(orcamentoId, lancamentoId, idUsuario)
        call.respond(HttpStatusCode.NoContent)
    }
}
