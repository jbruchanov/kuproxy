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
        val storedRequest = repo.find(domainRequest)
        if (storedRequest != null) {
            call.respond(storedRequest)
        } else {
            send(call, domainRequest)
        }
    }
}
