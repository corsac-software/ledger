package br.dev.brunorsch.config

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.setupPostgresDatabase(embedded: Boolean): Database {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        log.info("Using embedded H2 database for testing; replace this flag to use postgres")
        return Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            password = ""
        )
    } else {
        val url = environment.config.property("postgres.url").getString()
        log.info("Connecting to postgres database at $url")
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        return Database.connect(
            url = url,
            user = user,
            password = password
        )
    }
}
