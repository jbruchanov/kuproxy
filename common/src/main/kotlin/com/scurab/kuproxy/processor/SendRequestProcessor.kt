package com.scurab.kuproxy.processor

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.comm.Response
import com.scurab.kuproxy.common.BuildConfig
import com.scurab.kuproxy.ext.respond
import com.scurab.kuproxy.ext.toDomainHeaders
import io.ktor.application.ApplicationCall
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.ByteArrayContent
import io.ktor.request.receiveStream

typealias ProcessingCallback = ApplicationCall.() -> Unit

interface SendRequestProcessor {
    suspend fun send(call: ApplicationCall, request: IRequest): IResponse

    class Impl(
        private val client: HttpClient,
        private val customHandler: ProcessingCallback? = null
    ) : SendRequestProcessor {

        override suspend fun send(call: ApplicationCall, request: IRequest): IResponse {
            val requestBody = call.receiveStream().readBytes()
            val realResponse = client.request<HttpResponse>(request.url.toUrlString()) {
                method = HttpMethod(request.method)
                headers {
                    request.headers.forEach { (header, values) ->
                        when {
                            // TODO: 443 replace
                            header == "Host" -> append(header, values.replace(":443", ""))
                            IGNORED_HEADERS.contains(header.toLowerCase()) -> {
                                /*nothing*/
                            }
                            else -> append(header, values)
                        }
                    }
                }
                val contentType = request.headers.entries
                    .firstOrNull { it.key.equals(CONTENT_TYPE, ignoreCase = true) }
                    ?.let { ContentType.parse(it.value) }

                body = requestBody.takeIf { it.isNotEmpty() }
                    ?.let { ByteArrayContent(it, contentType = contentType) }
                    ?: EmptyContent
            }

            val domainResponse = with(realResponse) {
                Response(
                    status.value,
                    headers.toDomainHeaders(),
                    readBytes()
                )
            }

            call.respond(domainResponse) {
                customHandler?.invoke(this)
                AddKuProxyInfoHeader(this)
            }
            return domainResponse
        }
    }

    companion object {
        const val CONTENT_TYPE = "content-type"

        //ktor fails if we pass these headers manually,
        //added transitively in requestBody, lower case IMPORTANT!
        private val IGNORED_HEADERS = setOf("content-length", CONTENT_TYPE)
        val AddKuProxyInfoHeader: ProcessingCallback = {
            //TODO:version
            response.headers.append("KuProxy-Version", BuildConfig.AppVersion)
        }
    }
}
