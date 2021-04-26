package com.scurab.kuproxy.comm

import java.net.URI
import java.net.URLEncoder
import java.util.Objects

class Url(
    val scheme: String,
    val host: String,
    val port: Int,
    val path: String,
    val fragment: String,
    queryString: String
) {
    val queryStringMap: Map<String, String> = queryString
        .takeIf { it.isNotEmpty() }
        ?.splitToSequence("&")
        ?.map { it.split("=") }
        ?.filter { it.size == 2 }
        ?.map { Pair(it[0], it[1]) }
        ?.toMap()
        ?: emptyMap()

    constructor(uri: URI) : this(uri.scheme, uri.host, uri.port, uri.path ?: "", uri.fragment ?: "", uri.query ?: "")

    constructor(uri: String) : this(URI(uri))

    override fun toString() = toUrlString(false)

    fun toUrlString(escape: Boolean = true) = buildString {
        append(scheme).append("://")
        append(host)
        if (port > 0) {
            append(":").append(port)
        }
        if (path.isNotEmpty()) {
            append(path)
        }
        if (queryStringMap.isNotEmpty()) {
            append("?")
            append(
                queryStringMap.entries.joinToString("&") {
                    "${it.key}=${it.value.optEscape(escape)}"
                }
            )
        }
        if (fragment.isNotEmpty()) {
            append("#")
            append(fragment.optEscape(escape))
        }
    }

    /**
     * Escape string based on arg
     */
    private fun String.optEscape(escape: Boolean) =
        if (escape) URLEncoder.encode(this, Charsets.UTF_8.name()) else this

    override fun hashCode(): Int = Objects.hash(scheme, host, port, path, queryStringMap)

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other === this -> true
            other.javaClass == this.javaClass -> {
                (other as Url).let {
                    scheme == it.scheme &&
                        host == it.host &&
                        port == it.port &&
                        path == it.path &&
                        fragment == it.fragment &&
                        queryStringMap == it.queryStringMap
                }
            }
            else -> false
        }
    }
}
