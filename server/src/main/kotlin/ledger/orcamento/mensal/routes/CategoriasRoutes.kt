package br.dev.brunorsch.ledger.orcamento.mensal.routes

import br.dev.brunorsch.ledger.orcamento.mensal.api.CategoriaRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.CategoriaResponse
import br.dev.brunorsch.ledger.orcamento.mensal.api.CategoriaUpdateRequest
import br.dev.brunorsch.ledger.orcamento.mensal.api.CategoriasController
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
fun Route.categoriasRoutes(categoriasController: CategoriasController) {
    route("/api/orcamentos-mensais/categorias") {
        get { categoriasController.buscarTodas(call) }
            .describe {
                summary = "Listar categorias"
                description = "Lista as categorias do usuário."
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<List<CategoriaResponse>>()
                    }
                }
            }

        post { categoriasController.criar(call) }
            .describe {
                summary = "Criar categoria"
                description = "Cria uma categoria para o usuário."
                requestBody {
                    content {
                        schema = jsonSchema<CategoriaRequest>()
                    }
                }
                responses {
                    HttpStatusCode.Created {
                        schema = jsonSchema<CategoriaResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo inválido."
                    }
                }
            }

        post("/setup") { categoriasController.setupCategoriasPadrao(call) }
            .describe {
                summary = "Criar categorias padrão"
                description = "Cria as categorias padrão do usuário quando ele ainda não possui nenhuma categoria."
                responses {
                    HttpStatusCode.Created {
                        description = "As categorias padrão foram criadas."
                    }
                    HttpStatusCode.NoContent {
                        description = "O usuário já possui categoria cadastrada."
                    }
                }
            }

        put("/{categoriaId}") { categoriasController.atualizar(call) }
            .describe {
                summary = "Atualizar categoria"
                description = "Atualiza uma categoria por ID."
                requestBody {
                    content {
                        schema = jsonSchema<CategoriaUpdateRequest>()
                    }
                }
                responses {
                    HttpStatusCode.OK {
                        schema = jsonSchema<CategoriaResponse>()
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui corpo ou parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhuma categoria foi encontrada para o ID informado."
                    }
                }
            }

        delete("/{categoriaId}") { categoriasController.deletar(call) }
            .describe {
                summary = "Deletar categoria"
                description = "Deleta uma categoria por ID."
                responses {
                    HttpStatusCode.NoContent {
                        description = "A categoria foi deletada."
                    }
                    HttpStatusCode.BadRequest {
                        description = "A requisição possui parâmetros inválidos."
                    }
                    HttpStatusCode.NotFound {
                        description = "Nenhuma categoria foi encontrada para o ID informado."
                    }
                }
            }
    }.describe { tag("Categorias") }
}