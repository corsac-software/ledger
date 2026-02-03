package br.dev.brunorsch.config

import com.kborowy.authprovider.firebase.firebase
import io.ktor.http.*
import io.ktor.openapi.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import org.jetbrains.exposed.sql.*
import org.slf4j.event.*

fun Application.configureSecurity() {
    install(Authentication) {
        firebase {
            setup {
                adminFile = File("/pregs/firebase.json")
            }
            realm = "Pregs' Orçamento Familiar"
            validate { token ->
                print(token)
            }
        }
    }
}
