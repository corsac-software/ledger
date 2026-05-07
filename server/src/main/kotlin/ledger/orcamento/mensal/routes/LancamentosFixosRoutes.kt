package br.dev.brunorsch.ledger.orcamento.mensal.routes

import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoFixoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoFixoResponse
import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentoFixoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.LancamentosFixosController
import br.dev.brunorsch.ledger.utils.describeOrphan
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun Route.lancamentosFixosRoutes(controller: LancamentosFixosController) {
    route("/api/orcamentos-mensais/lancamentos-fixos") {
        get { controller.buscarTodos(call) }
            .describe {
                summary = "Listar lançamentos fixos"
                description = "Lista os lançamentos fixos ativos do usuário."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<LancamentoFixoResponse>>()
                    }
                }
            }

        get("/{lancamentoFixoId}") { controller.buscarPorId(call) }
            .describe {
                summary = "Consultar lançamento fixo por ID"
                description = "Consulta um lançamento fixo por ID."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<LancamentoFixoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum lançamento fixo foi encontrado para o ID informado."
                    }
                }
            }

        post { controller.criar(call) }
            .describe {
                summary = "Criar lançamento fixo"
                description = "Cria um lançamento fixo para o usuário."
                requestBody {
                    content {
                        schema = jsonSchema<LancamentoFixoRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<LancamentoFixoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo inválido."
                    }
                }
            }

        put("/{lancamentoFixoId}") { controller.atualizar(call) }
            .describe {
                summary = "Atualizar lançamento fixo"
                description = "Atualiza um lançamento fixo por ID."
                requestBody {
                    content {
                        schema = jsonSchema<LancamentoFixoUpdateRequest>()
                    }
                }
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<LancamentoFixoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum lançamento fixo foi encontrado para o ID informado."
                    }
                }
            }

        delete("/{lancamentoFixoId}") { controller.deletar(call) }
            .describe {
                summary = "Deletar lançamento fixo"
                description = "Deleta logicamente um lançamento fixo por ID."
                responses {
                    HttpStatusCode.NoContent {
                        description = "O lançamento fixo foi deletado."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum lançamento fixo foi encontrado para o ID informado."
                    }
                }
            }
    }.describeOrphan {
        tag("Lançamentos Fixos")
        tags
    }
}
