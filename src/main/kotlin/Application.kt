package br.dev.brunorsch

import br.dev.brunorsch.config.configureFrameworks
import br.dev.brunorsch.config.configureHTTP
import br.dev.brunorsch.config.configureMonitoring
import br.dev.brunorsch.config.configureReact
import br.dev.brunorsch.config.configureRouting
import br.dev.brunorsch.config.configureSecurity
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureFrameworks()
    configureRouting()
    configureReact()
}
