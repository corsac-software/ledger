package br.dev.brunorsch.config

import io.ktor.openapi.*
import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    install(ForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
    install(XForwardedHeaders) // WARNING: for security, do not include this if not behind a reverse proxy
    install(HSTS) {
        includeSubDomains = true
    }
    routing {
        swaggerUI(path = "docs") {
            info = OpenApiInfo(title = "Pregs' Orçamento Familiar", version = "1.0.0")
        }
    }
}
