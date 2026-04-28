package br.dev.brunorsch.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.configureRouting() {
    install(RequestValidation)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error(
                "Unhandled exception on ${call.request.httpMethod.value} ${call.request.uri}",
                cause,
            )
            call.respondText(
                text = "500: Internal Server Error",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }
}
