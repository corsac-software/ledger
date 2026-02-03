package br.dev.brunorsch.config

import com.kborowy.authprovider.firebase.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.io.File

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
