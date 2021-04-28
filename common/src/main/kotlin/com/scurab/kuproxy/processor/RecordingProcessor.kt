package com.scurab.kuproxy.processor

import com.scurab.kuproxy.ext.toDomainRequest
import com.scurab.kuproxy.storage.Repository
import com.scurab.kuproxy.storage.RequestResponse
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient

class RecordingProcessor(
    private val repo: Repository,
    client: HttpClient
) : ReplayProcessor(repo, client) {

    override suspend fun process(item: ApplicationCall) {
        val request = item.toDomainRequest()
        val response = send(item, request)
        repo.add(RequestResponse(request, response))
    }
}
