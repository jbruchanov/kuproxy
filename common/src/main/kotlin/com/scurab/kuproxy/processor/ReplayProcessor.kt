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

    override suspend fun process(call: ApplicationCall) {
        val domainRequest = call.toDomainRequest()
        val item = repo.find(domainRequest)
        if (item?.response != null) {
            call.respond(item.response)
        } else {
            send(call, domainRequest)
        }
    }
}
