package br.dev.brunorsch.config

import com.kborowy.authprovider.firebase.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.di.dependencies
import java.io.File

fun Application.configureSecurity() {
    val config: SecurityConfig by dependencies
    install(Authentication) {
        firebase {
            setup {
                adminFile = File(config.adminFile)
            }
            realm = "PregsLedger"
            validate { token ->
                print(token)
            }
        }
    }
}

data class SecurityConfig(
    val adminFile: String,
)