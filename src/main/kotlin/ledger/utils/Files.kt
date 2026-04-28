package br.dev.brunorsch.ledger.utils

import java.io.File

fun resolveDirectoryFromRoot(location: String): File {
    val projectRoot = File(System.getProperty("user.dir"))
    return projectRoot.resolve(location)
}