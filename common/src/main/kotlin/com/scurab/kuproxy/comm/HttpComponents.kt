package com.scurab.kuproxy.comm

typealias Headers = Map<String, Set<String>>

interface IRequest {
    val url: Url
    val method: String
    val headers: Headers
    val recorded: Long
}

interface IResponse {
    val status: Int
    val headers: Headers
    val body: ByteArray
}

class Request(
    override val url: Url,
    override val method: String,
    override val headers: Headers,
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
}

class Response(
    override val status: Int,
    override val headers: Headers,
    override val body: ByteArray
) : IResponse

interface RequestMatcher {
    fun isMatching(real: IRequest, stored: IRequest): Boolean
}

/**
 * Default request matcher.
 * URL must match per [Url.equals], Method must match exactly,
 * all stored headers must be included & be equal to real headers
 */
class DefaultRequestMatcher : RequestMatcher {

    override fun isMatching(real: IRequest, stored: IRequest): Boolean {
        return real.url == stored.url &&
            real.method == stored.method &&
            isMatchingHeaders(real.headers, stored.headers)
    }

    private fun isMatchingHeaders(real: Headers, stored: Headers): Boolean {
        return stored.all { (storedKey, storedValues) ->
            val realValues = real[storedKey] ?: return false
            realValues == storedValues
        }
    }
}
