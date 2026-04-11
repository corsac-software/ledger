package br.dev.brunorsch.config

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.OperationHiddenAttributeKey
import io.ktor.server.routing.openapi.hide
import io.ktor.utils.io.ExperimentalKtorApi

@OptIn(ExperimentalKtorApi::class)
fun Application.configureReact() {
    routing {
        singlePageApplication {
            react("web-app/dist")
        }
    }
}