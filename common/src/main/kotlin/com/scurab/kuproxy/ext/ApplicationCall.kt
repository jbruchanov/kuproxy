package com.scurab.kuproxy.ext

import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.content.ByteArrayContent
import io.ktor.request.header
import io.ktor.request.httpMethod
import io.ktor.request.receiveStream
import io.ktor.request.uri
import io.ktor.response.respondBytes
import java.net.URI

private val ignoredHeaders = setOf("content-length", "content-type")

suspend fun ApplicationCall.proxy(client: HttpClient) {
    val header = request.header("host")
    val url = request.uri
        .takeIf { it.startsWith("http") }
        ?: "https://${header}${request.uri}"

    val reqBody = this.receiveStream().readBytes()
    val destResponse = client.request<HttpResponse>(Url(URI.create(url))) {
        method = request.httpMethod
        headers {
            request.headers.forEach { header, values ->
                when {
                    // TODO: 443 replace
                    header == "Host" -> append(header, values.first().replace(":443", ""))
                    ignoredHeaders.contains(header.toLowerCase()) -> {
                        /*nothing*/
                    }
                    else -> appendAll(header, values)
                }
            }
        }
        val contentType = request.header("Content-Type")?.let { ContentType.parse(it) }
        body = reqBody.takeIf { it.isNotEmpty() }
            ?.let { ByteArrayContent(it, contentType = contentType) }
            ?: EmptyContent
    }

    response.status(destResponse.status)

    destResponse.headers.forEach { headerName, headerValues ->
        headerValues.forEach { headerValue ->
            response.headers.append(headerName, headerValue, safeOnly = false)
        }
    }

    response.headers.append("Custom-Proxy", "KuProxy")
    val bytes = destResponse.readBytes()
    respondBytes(bytes)
}
