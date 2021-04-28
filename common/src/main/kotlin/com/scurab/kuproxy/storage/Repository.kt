package com.scurab.kuproxy.storage

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse

interface Repository {
    val items: List<RequestResponse>
    suspend fun find(request: IRequest): RequestResponse?
    suspend fun add(item: RequestResponse)
    suspend fun remove(item: RequestResponse): Boolean
}

open class RequestResponse(
    val request: IRequest,
    val response: IResponse
) {
    operator fun component1() = request
    operator fun component2() = response
}
