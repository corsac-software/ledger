package br.dev.brunorsch.ledger.orcamento.mensal

import br.dev.brunorsch.ledger.utils.withMigrationGenerationEnabled
import br.dev.brunorsch.ledger.orcamento.mensal.api.*
import br.dev.brunorsch.ledger.orcamento.mensal.data.gerarOrcamentoMensalMigrationScripts
import br.dev.brunorsch.ledger.orcamento.mensal.data.repository.OrcamentosMensaisRepository
import br.dev.brunorsch.ledger.orcamento.mensal.service.OrcamentosMensaisService
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun Application.orcamentoMensalModule() {
    withMigrationGenerationEnabled {
        gerarOrcamentoMensalMigrationScripts()
    }

    dependencies {
        provide { OrcamentosMensaisRepository() }
        provide { OrcamentosMensaisService(resolve()) }
        provide { OrcamentosMensaisController(resolve()) }
    }

    val controller: OrcamentosMensaisController by dependencies

    routing {
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
}