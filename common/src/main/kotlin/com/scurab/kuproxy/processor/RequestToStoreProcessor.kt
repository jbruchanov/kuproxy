package com.scurab.kuproxy.processor

import com.scurab.kuproxy.comm.DomainHeaders
import com.scurab.kuproxy.comm.Request
import com.scurab.kuproxy.storage.RequestResponse

class RequestToStoreProcessor(
    private val headerFilter: (String) -> Boolean = FILTER_NO_HEADERS
) : Processor<RequestResponse, RequestResponse> {

    override suspend fun process(item: RequestResponse): RequestResponse {
        return RequestResponse(
            Request(
                item.request.url,
                headers = item.request.headers.filtered(headerFilter),
                method = item.request.method,
                recorded = item.request.recorded
            ),
            item.response
        )
    }

    private fun DomainHeaders.filtered(predicate: (String) -> Boolean): DomainHeaders {
        return if (predicate == FILTER_NO_HEADERS) {
            emptyMap()
        } else {
            filterKeys(predicate)
        }
    }

    companion object {
        val FILTER_NO_HEADERS: (String) -> Boolean = { _: String -> false }
    }
}