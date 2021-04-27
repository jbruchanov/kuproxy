package com.scurab.kuproxy.storage

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse

interface Repository {
    val items: List<RequestResponse>
    fun find(request: IRequest): IResponse?
    fun add(item: RequestResponse)
}

open class RequestResponse(
    val request: IRequest,
    val response: IResponse
)
