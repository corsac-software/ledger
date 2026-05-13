package br.dev.corsac.ledger.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime

fun LocalDateTime.Companion.now(): LocalDateTime {
    return java.time.LocalDateTime.now().toKotlinLocalDateTime()
}
