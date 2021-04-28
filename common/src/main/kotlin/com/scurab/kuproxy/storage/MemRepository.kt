package com.scurab.kuproxy.storage

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.matcher.DefaultRequestMatcher
import com.scurab.kuproxy.matcher.RequestMatcher
import com.scurab.kuproxy.model.Tape
import com.scurab.kuproxy.processor.Processor
import com.scurab.kuproxy.processor.RequestToStoreProcessor

open class MemRepository(
    private val matcher: RequestMatcher = DefaultRequestMatcher(),
    private val processor: Processor<RequestResponse, RequestResponse> = RequestToStoreProcessor()
) : Repository {

    constructor(
        tape: Tape,
        matcher: RequestMatcher = DefaultRequestMatcher(),
        processor: Processor<RequestResponse, RequestResponse> = RequestToStoreProcessor()
    ) : this(matcher, processor) {
        _items.addAll(tape.interactions)
    }

    private val _items = mutableListOf<RequestResponse>()

    val itemsCount get() = _items.size

    override val items: List<RequestResponse>
        get() = synchronized(_items) {
            _items.toList()
        }

    override suspend fun find(request: IRequest): RequestResponse? {
        return _items
            .findLast { matcher.isMatching(request, it.request) }
    }

    override suspend fun add(item: RequestResponse) {
        val storingItem = processor.process(item)
        synchronized(_items) {
            _items.add(storingItem)
        }
    }

    override suspend fun remove(item: RequestResponse): Boolean {
        return synchronized(_items) {
            _items.remove(item)
        }
    }
}
