package com.scurab.kuproxy.storage

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.matcher.RequestMatcher
import com.scurab.kuproxy.model.Tape

open class MemRepository(
    private val matcher: RequestMatcher
) : Repository {

    constructor(tape: Tape, matcher: RequestMatcher) : this(matcher) {
        _items.addAll(tape.interactions)
    }

    private val _items = mutableListOf<RequestResponse>()

    val itemsCount get() = _items.size

    override val items: List<RequestResponse>
        get() = synchronized(_items) {
            _items.toList()
        }

    override fun find(request: IRequest): RequestResponse? {
        return _items
            .findLast { matcher.isMatching(request, it.request) }
    }

    override fun add(item: RequestResponse) {
        synchronized(_items) {
            _items.add(item)
        }
    }
}
