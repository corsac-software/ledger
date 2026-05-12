package br.dev.brunorsch.ledger.orcamento.mensal.routes

import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoResponse
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.LancamentoUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.OrcamentoMensalRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.OrcamentoMensalResponse
import br.dev.brunorsch.ledger.orcamento.mensal.api.OrcamentosMensaisController
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
fun Route.orcamentosMensaisRoutes(controller: OrcamentosMensaisController) {
    route("/api/orcamentos-mensais") {
        get { controller.buscarTodos(call) }
            .describe {
                summary = "Listar orçamentos mensais"
                description = "Lista os orçamentos mensais do usuário."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<OrcamentoMensalResponse>>()
                    }
                }
            }

        get("/{id}") { controller.buscarPorId(call) }
            .describe {
                summary = "Consultar orçamento mensal por ID"
                description = "Consulta um orçamento mensal por ID."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<OrcamentoMensalResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum orçamento mensal foi encontrado para o ID informado."
                    }
                }
            }

        get("/{id}/lancamentos") { controller.buscarLancamentosPorId(call) }
            .describe {
                summary = "Consultar lançamentos de um orçamento mensal"
                description = "Consulta os lançamentos de um orçamento mensal por ID."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<LancamentoResponse>>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum orçamento mensal foi encontrado para o ID informado."
                    }
                }
            }

        post { controller.criar(call) }
            .describe {
                summary = "Criar orçamento mensal"
                description = "Cria um novo orçamento mensal para o usuário informado."
                requestBody {
                    content {
                        schema = jsonSchema<OrcamentoMensalRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<OrcamentoMensalResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                }
            }

        delete("/{id}") { controller.excluir(call) }
            .describe {
                summary = "Excluir orçamento mensal"
                description = "Exclui um orçamento mensal pelo ID informado."
                responses {
                    HttpStatusCode.NoContent {
                        description = "O orçamento mensal foi excluído com sucesso."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum orçamento mensal foi encontrado para o ID informado."
                    }
                }
            }

        post("/{id}/lancamentos") { controller.criarLancamento(call) }
            .describe {
                summary = "Criar lançamento em orçamento mensal"
                description = "Cria um lançamento vinculado ao orçamento mensal informado."
                requestBody {
                    content {
                        schema = jsonSchema<LancamentoRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<LancamentoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum orçamento mensal foi encontrado para o ID informado."
                    }
                }
            }

        post("/{id}/lancamentos-fixos/importar") { controller.importarLancamentosFixos(call) }
            .describe {
                summary = "Importar lançamentos fixos"
                description = "Cria lançamentos mensais a partir dos lançamentos fixos válidos para o mês do orçamento."
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<List<LancamentoResponse>>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum orçamento mensal foi encontrado para o ID informado."
                    }
                }
            }

        put("/{id}/lancamentos/{lancamentoId}") { controller.atualizarLancamento(call) }
            .describe {
                summary = "Atualizar lançamento em orçamento mensal"
                description = "Atualiza um lançamento de um orçamento mensal pelos IDs informados."
                requestBody {
                    content {
                        schema = jsonSchema<LancamentoUpdateRequest>()
                    }
                }
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<LancamentoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "O orçamento mensal ou lançamento não foi encontrado para os IDs informados."
                    }
                }
            }

        delete("/{id}/lancamentos/{lancamentoId}") { controller.excluirLancamento(call) }
            .describe {
                summary = "Excluir lançamento de orçamento mensal"
                description = "Exclui um lançamento de um orçamento mensal pelos IDs informado."
                responses {
                    HttpStatusCode.NoContent {
                        description = "O lançamento foi excluído com sucesso."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "O orçamento mensal ou lançamento não foi encontrado para os IDs informados."
                    }
                }
            }
    }.describe { tag("Orçamentos Mensais") }
}
