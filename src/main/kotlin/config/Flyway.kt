package br.dev.brunorsch.config

import org.flywaydb.core.Flyway
import javax.sql.DataSource

const val MIGRATIONS_DIR = "migrations"

fun setupFlyway(
    dataSource: DataSource,
    firstTime: Boolean
) = Flyway.configure()
    .dataSource(dataSource)
    .locations("filesystem:$MIGRATIONS_DIR")
    .baselineOnMigrate(firstTime)
    .load()