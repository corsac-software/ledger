package br.dev.brunorsch.config

import org.flywaydb.core.Flyway
import javax.sql.DataSource

const val GENERATED_MIGRATIONS_DIR = "generated-migrations"
const val MIGRATIONS_DIR = "server/migrations"

fun setupFlyway(
    dataSource: DataSource
): Flyway {
    return Flyway.configure()
        .dataSource(dataSource)
        .locations("filesystem:$MIGRATIONS_DIR")
        .load()
}