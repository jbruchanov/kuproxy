package com.scurab.kuproxy.comm

import io.ktor.utils.io.charsets.Charset

object Headers {
    const val CharSet = "charset"
    const val ContentType = "content-type"
    const val ContentLength = "content-length"
    const val AcceptEncoding = "accept-encoding"
    const val KuProxyVersion = "kuproxy-version"

    fun String.isTextContent() = contains("text/", ignoreCase = true) ||
        contains("xml", ignoreCase = true) ||
        contains("javascript", ignoreCase = true) ||
        contains("json", ignoreCase = true)

    fun String.isImageContent() = contains("image/", ignoreCase = true)

    fun String.isVideoContent() = contains("video/", ignoreCase = true)

    fun String.headerCharset(default: Charset = Charset.defaultCharset()): Charset =
        substringAfter("$CharSet=")
            .substringBefore(";")
            .takeIf { it.isNotEmpty() }
            ?.let {
                kotlin.runCatching { Charset.forName(it) }.getOrNull()
            } ?: default
}
