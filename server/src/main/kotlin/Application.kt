package br.dev.corsac.ledger

import br.dev.corsac.ledger.config.*
import br.dev.corsac.ledger.orcamento.mensal.orcamentoMensalModule
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    configureFrameworks()
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureRouting()
    configureReact()

    // Módulos por funcionalidade
    orcamentoMensalModule()
}
