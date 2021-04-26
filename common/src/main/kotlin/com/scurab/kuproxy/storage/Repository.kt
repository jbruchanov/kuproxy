package com.scurab.kuproxy.storage

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.comm.RequestMatcher

interface Repository {
    val items: List<RequestResponse>
    fun find(request: IRequest): IResponse?
    fun add(item: RequestResponse)
}

open class RequestResponse(
    val request: IRequest,
    val response: IResponse
)

open class MemRepository(
    private val matcher: RequestMatcher
) : Repository {

    private val _items = mutableListOf<RequestResponse>()

    val itemsCount get() = _items.size

    override val items: List<RequestResponse>
        get() = synchronized(_items) {
            _items.toList()
        }

    override fun find(request: IRequest): IResponse? {
        return _items
            .findLast { matcher.isMatching(request, it.request) }
            ?.response
    }

    override fun add(item: RequestResponse) {
        synchronized(_items) {
            _items.add(item)
        }
    }
}
