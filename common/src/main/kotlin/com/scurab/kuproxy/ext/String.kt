package com.scurab.kuproxy.ext

fun String.toPortNumberOrNull(): Int? = toIntOrNull()?.takeIf { it in 1..65535 }
fun String.toPortNumber(): Int = toPortNumberOrNull() ?: throw IllegalStateException("Invalid port number:'$this'")
fun String.toDomainRegex(): Regex = ("\\Q$this\\E").replace("*", "\\E.*\\Q").toRegex()
