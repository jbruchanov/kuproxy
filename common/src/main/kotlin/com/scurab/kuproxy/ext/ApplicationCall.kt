package com.scurab.kuproxy.ext

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.comm.Request
import com.scurab.kuproxy.comm.Url
import io.ktor.application.ApplicationCall
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.response.respond

fun ApplicationCall.toDomainRequest(): IRequest {
    val hostHeader = request.header("host")
    val url = request.uri
        .takeIf { it.startsWith("http") }
        ?: "https://${hostHeader}${request.uri}"

    return Request(
        Url(url),
        request.httpMethod.value,
        headers = request.headers.toDomainHeaders()
    )
}

suspend fun ApplicationCall.respond(domainResponse: IResponse, block: ApplicationCall.() -> Unit = {}) {
    val realResponse = this.response
    realResponse.status(HttpStatusCode.fromValue(domainResponse.status))
    domainResponse.headers.forEach { (headerName, headerValue) ->
        realResponse.headers.append(headerName, headerValue, safeOnly = false)
    }
    apply(block)
    this.respond(domainResponse.body)
}
