package com.scurab.kuproxy.processor

import com.scurab.kuproxy.ext.toDomainRequest
import com.scurab.kuproxy.storage.Repository
import com.scurab.kuproxy.storage.RequestResponse
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient

class SavingProcessor(
    private val repo: Repository,
    client: HttpClient
) : ReplayProcessor(repo, client) {

    override suspend fun process(call: ApplicationCall) {
        val request = call.toDomainRequest()
        val response = send(call, request)
        repo.add(RequestResponse(request, response))
    }
}
