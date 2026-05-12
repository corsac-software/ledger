package br.dev.brunorsch.ledger.orcamento.mensal.routes

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.ParcelamentoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.ParcelamentoResponse
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.ParcelamentoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.ParcelamentosController
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
fun Route.parcelamentosRoutes(controller: ParcelamentosController) {
    route("/{cartaoId}/parcelamentos") {
        get { controller.buscarTodos(call) }
            .describe {
                summary = "Listar parcelamentos"
                description = "Lista os parcelamentos ativos de um cartão."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<ParcelamentoResponse>>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum cartão foi encontrado para o ID informado."
                    }
                }
            }

        get("/{parcelamentoId}") { controller.buscarPorId(call) }
            .describe {
                summary = "Consultar parcelamento por ID"
                description = "Consulta um parcelamento de um cartão por ID."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<ParcelamentoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum parcelamento foi encontrado para o ID informado."
                    }
                }
            }

        post { controller.criar(call) }
            .describe {
                summary = "Criar parcelamento"
                description = "Cria um parcelamento para um cartão."
                requestBody {
                    content {
                        schema = jsonSchema<ParcelamentoRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<ParcelamentoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo inválido."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum cartão foi encontrado para o ID informado."
                    }
                }
            }

        put("/{parcelamentoId}") { controller.atualizar(call) }
            .describe {
                summary = "Atualizar parcelamento"
                description = "Atualiza um parcelamento de um cartão por ID."
                requestBody {
                    content {
                        schema = jsonSchema<ParcelamentoUpdateRequest>()
                    }
                }
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<ParcelamentoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum parcelamento foi encontrado para o ID informado."
                    }
                }
            }

        delete("/{parcelamentoId}") { controller.deletar(call) }
            .describe {
                summary = "Deletar parcelamento"
                description = "Deleta logicamente um parcelamento de um cartão por ID."
                responses {
                    HttpStatusCode.NoContent {
                        description = "O parcelamento foi deletado."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum parcelamento foi encontrado para o ID informado."
                    }
                }
            }
    }
}
