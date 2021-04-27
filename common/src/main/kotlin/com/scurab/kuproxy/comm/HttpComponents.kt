package com.scurab.kuproxy.comm

typealias DomainHeaders = Map<String, String>

interface IRequest {
    val url: Url
    val method: String
    val headers: DomainHeaders
    val recorded: Long
}

interface IResponse {
    val status: Int
    val headers: DomainHeaders
    val body: ByteArray

    val notEmptyBody: ByteArray? get() = body.takeIf { it.isNotEmpty() }
}

class Request(
    override val url: Url,
    override val method: String,
    override val headers: DomainHeaders,
    override val recorded: Long = System.currentTimeMillis()
) : IRequest {

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


}
