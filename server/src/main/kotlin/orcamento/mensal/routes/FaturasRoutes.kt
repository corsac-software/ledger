package br.dev.corsac.ledger.orcamento.mensal.routes

import br.dev.corsac.ledger.orcamento.mensal.api.FaturasController
import br.dev.corsac.ledger.orcamento.mensal.api.dtos.FaturaRequest
import br.dev.corsac.ledger.orcamento.mensal.api.dtos.FaturaResponse
import br.dev.corsac.ledger.orcamento.mensal.api.dtos.FaturaUpdateRequest
import br.dev.corsac.ledger.utils.describeOrphan
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun Route.faturasRoutes(
    controller: FaturasController
) {
    route("/faturas") {
        get { controller.buscarTodos(call) }
            .describe {
                summary = "Listar faturas"
                description = "Lista as faturas de um cartão."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<FaturaResponse>>()
                    }
                }
            }

        get("/{faturaId}") { controller.buscarPorId(call) }
            .describe {
                summary = "Consultar fatura por ID"
                description = "Consulta uma fatura por ID."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<FaturaResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhuma fatura foi encontrada para o ID informado."
                    }
                }
            }

        post { controller.criar(call) }
            .describe {
                summary = "Criar fatura"
                description = "Cria uma fatura e vincula a um lançamento no orçamento mensal informado."
                requestBody {
                    content {
                        schema = jsonSchema<FaturaRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<FaturaResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo inválido."
                    }
                    HttpStatusCode.NotFound {
                        description = "Cartão não encontrado."
                    }
                }
            }

        put("/{faturaId}") { controller.atualizar(call) }
            .describe {
                summary = "Atualizar fatura"
                description = "Atualiza uma fatura por ID."
                requestBody {
                    content {
                        schema = jsonSchema<FaturaUpdateRequest>()
                    }
                }
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<FaturaResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhuma fatura foi encontrada para o ID informado."
                    }
                }
            }

        delete("/{faturaId}") { controller.deletar(call) }
            .describe {
                summary = "Deletar fatura"
                description = "Deleta uma fatura por ID. O lançamento vinculado permanece no orçamento."
                responses {
                    HttpStatusCode.NoContent {
                        description = "A fatura foi deletada."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhuma fatura foi encontrada para o ID informado."
                    }
                }
            }
    }.describeOrphan { tag("Faturas") }
}