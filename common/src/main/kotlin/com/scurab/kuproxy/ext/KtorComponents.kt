package com.scurab.kuproxy.ext

import com.scurab.kuproxy.comm.DomainHeaders
import io.ktor.http.Headers as KtorHeaders

fun KtorHeaders.toDomainHeaders(): DomainHeaders = entries().associateBy(
    keySelector = { it.key },
    valueTransform = { it.value.joinToString(separator = "; ") }
)
