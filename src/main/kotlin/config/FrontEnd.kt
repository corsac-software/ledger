package br.dev.brunorsch.config

import io.ktor.server.application.Application
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.routing

fun Application.configureReact() {
    routing {
        singlePageApplication {
            react("web-app/dist")
        }
    }
}