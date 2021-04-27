package com.scurab.kuproxy.storage

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.matcher.RequestMatcher

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
