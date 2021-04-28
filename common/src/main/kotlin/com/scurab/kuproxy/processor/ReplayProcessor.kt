package com.scurab.kuproxy.processor

import com.scurab.kuproxy.ext.toDomainRequest
import com.scurab.kuproxy.storage.Repository
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient
import io.ktor.response.respond

open class ReplayProcessor(
    private val repo: Repository,
    client: HttpClient
) : KtorProcessor,
    SendRequestProcessor by SendRequestProcessor.Impl(client) {

    override suspend fun process(item: ApplicationCall) {
        val domainRequest = item.toDomainRequest()
        val requestResponse = repo.find(domainRequest)
        if (requestResponse?.response != null) {
            item.respond(item.response)
        } else {
            send(item, domainRequest)
        }
    }
}
