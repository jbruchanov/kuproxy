package com.scurab.kuproxy.comm

typealias DomainHeaders = Map<String, String>

interface IRequestResponseCommon {
    val headers: DomainHeaders
    val body: ByteArray

    val notEmptyBody: ByteArray? get() = body.takeIf { it.isNotEmpty() }
}

interface IRequest : IRequestResponseCommon {
    val url: Url
    val method: String
    override val headers: DomainHeaders
    val recorded: Long
}

interface IResponse : IRequestResponseCommon {
    val status: Int
    override val headers: DomainHeaders
    override val body: ByteArray
}

class Request(
    override val url: Url,
    override val method: String,
    override val headers: DomainHeaders,
    override val recorded: Long = System.currentTimeMillis()
) : IRequest {

    //include body into equals ?
    override var body: ByteArray = EMPTY
        private set

    constructor(
        url: Url,
        method: String,
        headers: DomainHeaders,
        body: ByteArray,
        recorded: Long = System.currentTimeMillis()
    ) : this(url, method, headers, recorded) {
        this.body = body
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Request

        if (url != other.url) return false
        if (method != other.method) return false
        if (headers != other.headers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + headers.hashCode()
        return result
    }

    override fun toString(): String {
        return "Request(url=$url, method='$method', headers=$headers, recorded=$recorded)"
    }

    companion object {
        private val EMPTY = ByteArray(0)
    }
}

class Response(
    override val status: Int,
    override val headers: DomainHeaders,
    override val body: ByteArray
) : IResponse {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Response

        if (status != other.status) return false
        if (headers != other.headers) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "Response(status=$status, headers=$headers, bodyLen=${body.size})"
    }

    companion object {
        val EMPTY = Response(-1, emptyMap(), ByteArray(0))
    }
}
