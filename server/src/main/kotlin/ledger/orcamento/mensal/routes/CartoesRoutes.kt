package br.dev.brunorsch.ledger.orcamento.mensal.routes

import br.dev.brunorsch.ledger.orcamento.mensal.api.CartoesController
import br.dev.brunorsch.ledger.orcamento.mensal.api.FaturasController
import br.dev.brunorsch.ledger.orcamento.mensal.api.ParcelamentosController
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CartaoRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CartaoResponse
import br.dev.brunorsch.ledger.orcamento.mensal.api.dtos.CartaoUpdateRequest
import br.dev.brunorsch.ledger.utils.describeOrphan
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun Route.cartoesRoutes(
    controller: CartoesController,
    parcelamentosController: ParcelamentosController,
    faturasController: FaturasController
) {
    route("/api/orcamentos-mensais/cartoes") {
        get { controller.buscarTodos(call) }
            .describe {
                summary = "Listar cartões"
                description = "Lista os cartões ativos do usuário."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<CartaoResponse>>()
                    }
                }
            }

        get("/{cartaoId}") { controller.buscarPorId(call) }
            .describe {
                summary = "Consultar cartão por ID"
                description = "Consulta um cartão por ID."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<CartaoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum cartão foi encontrado para o ID informado."
                    }
                }
            }

        post { controller.criar(call) }
            .describe {
                summary = "Criar cartão"
                description = "Cria um cartão para o usuário."
                requestBody {
                    content {
                        schema = jsonSchema<CartaoRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<CartaoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo inválido."
                    }
                }
            }

        put("/{cartaoId}") { controller.atualizar(call) }
            .describe {
                summary = "Atualizar cartão"
                description = "Atualiza um cartão por ID."
                requestBody {
                    content {
                        schema = jsonSchema<CartaoUpdateRequest>()
                    }
                }
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<CartaoResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum cartão foi encontrado para o ID informado."
                    }
                }
            }

        delete("/{cartaoId}") { controller.deletar(call) }
            .describe {
                summary = "Deletar cartão"
                description = "Deleta logicamente um cartão por ID."
                responses {
                    HttpStatusCode.NoContent {
                        description = "O cartão foi deletado."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhum cartão foi encontrado para o ID informado."
                    }
                }
            }

        parcelamentosRoutes(parcelamentosController)
        faturasRoutes(faturasController)
    }.describeOrphan { tag("Cartões") }
}
