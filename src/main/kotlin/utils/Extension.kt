package br.dev.brunorsch.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val idNaoInserido: Long = -1

inline fun <reified T> T.slf4j(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}