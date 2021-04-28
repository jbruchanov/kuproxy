package com.scurab.kuproxy.processor

import com.scurab.kuproxy.ext.toDomainRequest
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

open class PassThroughProcessor(
    client: HttpClient = HttpClient(CIO) { expectSuccess = false }
) : KtorProcessor,
    SendRequestProcessor by SendRequestProcessor.Impl(client) {

    override suspend fun process(item: ApplicationCall) {
        send(item, item.toDomainRequest())
    }
}
