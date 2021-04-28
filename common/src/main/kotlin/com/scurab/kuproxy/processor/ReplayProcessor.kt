package com.scurab.kuproxy.processor

import com.scurab.kuproxy.ext.respond
import com.scurab.kuproxy.ext.toDomainRequest
import com.scurab.kuproxy.storage.Repository
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient

open class ReplayProcessor(
    private val repo: Repository,
    client: HttpClient
) : KtorProcessor,
    SendRequestProcessor by SendRequestProcessor.Impl(client) {

    override suspend fun process(item: ApplicationCall) {
        val domainRequest = item.toDomainRequest()
        val stored = repo.find(domainRequest)
        if (stored != null) {
            item.respond(stored.response)
        } else {
            send(item, domainRequest)
        }
    }
}
