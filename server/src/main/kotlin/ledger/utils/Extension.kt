package br.dev.brunorsch.ledger.utils

import io.ktor.server.routing.*
import io.ktor.server.routing.openapi.*
import io.ktor.utils.io.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val idNaoInserido: Long = -1

inline fun <reified T> T.slf4j(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

// TODO: Remover (Não funcionou)
@OptIn(ExperimentalKtorApi::class)
fun Route.describeOrphan(configure: RouteOperationFunction) {
    describe {}
        .apply { this.attributes.remove(OperationDescribeAttributeKey) }
        .describe(configure)
}