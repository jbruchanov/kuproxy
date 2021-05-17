package com.scurab.kuproxy.desktop.ext

public inline fun <T, R> Iterable<T>.mapCatching(transform: (T) -> R): List<R?> {
    return map { kotlin.runCatching { transform(it) }.getOrNull() }
}

public fun <T, R> Sequence<T>.mapCatching(transform: (T) -> R): Sequence<R> {
    return map { kotlin.runCatching { transform(it) }.getOrNull() }.filterNotNull()
}
