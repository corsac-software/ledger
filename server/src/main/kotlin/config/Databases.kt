package br.dev.brunorsch.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import org.jetbrains.exposed.v1.jdbc.Database


data class DatabaseConfig(
    val h2DbPath: String,
)

fun Application.setupHikari(embedded: Boolean): HikariDataSource {
    val dbConfig: DatabaseConfig by dependencies
    val config = HikariConfig().apply {
        if(embedded) {
            val dbPath = dbConfig.h2DbPath
            val embeddedUrl = "jdbc:h2:file:$dbPath;AUTO_SERVER=TRUE;MODE=PostgreSQL"
            log.info("Usando H2 database do arquivo: $dbPath")

            driverClassName = "org.h2.Driver"
            jdbcUrl = embeddedUrl
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
