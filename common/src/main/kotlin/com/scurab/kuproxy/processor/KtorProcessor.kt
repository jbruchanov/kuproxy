package com.scurab.kuproxy.processor

import io.ktor.application.ApplicationCall

interface KtorProcessor {
    suspend fun process(call: ApplicationCall)
}
