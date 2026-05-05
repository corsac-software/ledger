package br.dev.brunorsch.config

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

@OptIn(ExperimentalKtorApi::class)
fun Application.configureReact() {
    routing {
        singlePageApplication {
            react("web/dist")
        }
    }
}