package com.scurab.kuproxy.ext

import com.scurab.kuproxy.comm.Headers
import io.ktor.http.Headers as KtorHeaders

fun KtorHeaders.toDomainHeaders(): Headers = entries().associateBy(
    keySelector = { it.key },
    valueTransform = { it.value.joinToString(separator = "; ") }
)
