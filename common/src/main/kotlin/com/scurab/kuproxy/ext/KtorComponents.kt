package com.scurab.kuproxy.ext

import com.scurab.kuproxy.comm.Headers
import io.ktor.http.Headers as KtorHeaders

fun KtorHeaders.toDomainHeaders(): Headers {
    val headers = mutableMapOf<String, MutableSet<String>>()
    forEach { key, values ->
        headers.getOrPut(key) { mutableSetOf() }.addAll(values)
    }
    return headers
}
