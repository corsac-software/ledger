package br.dev.brunorsch.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database

const val EMBEDDED_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
const val EMBEDDED_USER = "root"
const val EMBEDDED_PASSWORD = ""

fun Application.setupHikari(embedded: Boolean): HikariDataSource {

    val config = HikariConfig().apply {
        if(embedded) {
            log.info("Using embedded H2 database for testing; replace this flag to use postgres")

            driverClassName = "org.h2.Driver"
            jdbcUrl = EMBEDDED_URL
            username = EMBEDDED_USER
            password = EMBEDDED_PASSWORD
        } else {
            Class.forName("org.postgresql.Driver")

            val url = environment.config.property("postgres.url").getString()
            log.info("Connecting to postgres database at $url")
            val user = environment.config.property("postgres.user").getString()
            val dbPassword = environment.config.property("postgres.password").getString()

            driverClassName = "org.postgresql.Driver"
            jdbcUrl = url
            username = user
            password = dbPassword
        }

        maximumPoolSize = 6
        isReadOnly = false
        transactionIsolation = "TRANSACTION_SERIALIZABLE"
    }

    return HikariDataSource(config)
}

fun setupPostgresDatabase(hikariDataSource: HikariDataSource): Database {
    return Database.connect(hikariDataSource)
}
