package com.scurab.kuproxy.processor

import io.ktor.application.ApplicationCall

interface Processor<I, O> {
    suspend fun process(item: I): O
}

interface KtorProcessor : Processor<ApplicationCall, Unit> {
    override suspend fun process(item: ApplicationCall)
}
