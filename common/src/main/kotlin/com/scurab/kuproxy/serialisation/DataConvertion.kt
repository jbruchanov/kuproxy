package com.scurab.kuproxy.serialisation

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.scurab.kuproxy.comm.DomainHeaders
import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.comm.Request
import com.scurab.kuproxy.comm.Response
import com.scurab.kuproxy.comm.Url
import com.scurab.kuproxy.model.Tape
import com.scurab.kuproxy.storage.RequestResponse
import java.io.StringReader

private typealias Object = Map<String, Any>

@Suppress("UNCHECKED_CAST")
private operator fun <T> Map<String, Any>.get(field: Field<T>) = this[field.fieldName] as T

private fun obj(vararg pairs: Pair<Field<*>, Any?>): Object =
    if (pairs.isNotEmpty()) {
        @Suppress("UNCHECKED_CAST")
        pairs.filter { it.second != null }
            .associateBy(
                keySelector = { it.first.fieldName },
                valueTransform = { it.second }
            ) as Object
    } else emptyMap()

class Field<T> private constructor(val fieldName: String) {
    companion object {
        val Name = Field<String>("name")
        val Interactions = Field<List<Object>>("interactions")
        val Request = Field<Object>("request")
        val Response = Field<Object>("response")
        val Url = Field<String>("url")
        val Method = Field<String>("method")
        val Headers = Field<Map<String, String>?>("headers")
        val Status = Field<Int>("status")
        val Recorded = Field<String>("recorded")
        val Body = Field<Any?>("body")
    }
}

open class TapeExportConverter(
    private val dateLongConverter: DateLongConverter = DateLongConverter(),
    private val headersToStore: Set<String>? = emptySet(),
    private val textContentHeaders: List<Regex> = TEXT_CONTENT_HEADERS
) {
    fun convert(item: Tape): Map<String, Any> = obj(
        Field.Name to item.name,
        Field.Interactions to item.interactions.map { (req, resp) ->
            obj(
                Field.Request to obj(
                    Field.Url to req.url.toString(),
                    Field.Method to req.method,
                    Field.Headers to req.filteredHeaders(headersToStore),
                    Field.Recorded to dateLongConverter.convert(req.recorded),
                ),
                Field.Response to obj(
                    Field.Status to resp.status,
                    Field.Headers to resp.headers,
                    Field.Body to resp.serialisedBody()
                )
            )
        }
    )

    private fun IRequest.filteredHeaders(headersToStore: Set<String>?): DomainHeaders? =
        headers
            .filterKeys { headersToStore?.contains(it.toLowerCase()) ?: true }
            .takeIf { it.isNotEmpty() }

    private fun IResponse.serialisedBody(): Any? {
        val contentType = headers.entries
            .firstOrNull { Headers.ContentType.equals(it.key, ignoreCase = true) }
            ?.value
            ?.toLowerCase()

        return notEmptyBody
            ?.takeIf { contentType != null && textContentHeaders.firstOrNull { it.containsMatchIn(contentType) } != null }
            ?.decodeToString()
            ?.let {
                if (contentType?.contains("json") == true) {
                    gson.toJson(JsonParser.parseReader(StringReader(it)))
                } else {
                    it
                }
            }
            ?: notEmptyBody
    }

    companion object {
        //TODO: extract as dependency
        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val TEXT_CONTENT_HEADERS = listOf(
            "text\\/.*",
            "application\\/json",
            "application\\/xml",
            "application\\/javascript",
            "application\\/yaml",
        ).map { it.toRegex() }
    }
}

open class TapeImportConverter(
    private val dateLongConverter: DateLongConverter = DateLongConverter()
) {
    fun convert(item: Map<String, Any>): Tape {
        return Tape(
            name = item[Field.Name],
            interactions = (item[Field.Interactions]).map { interaction ->
                val request = interaction[Field.Request]
                val response = interaction[Field.Response]
                RequestResponse(
                    Request(
                        url = Url(request[Field.Url]),
                        method = request[Field.Method],
                        headers = request[Field.Headers] ?: emptyMap(),
                        recorded = dateLongConverter.convert(request[Field.Recorded])
                    ),
                    Response(
                        status = response[Field.Status],
                        headers = response[Field.Headers] ?: emptyMap(),
                        body = response[Field.Body]?.body() ?: ByteArray(0)
                    )
                )
            }
        )
    }

    private fun Any.body(): ByteArray = when (this) {
        is String -> toByteArray()
        is ByteArray -> this
        else -> throw IllegalStateException("Invalid body type:${javaClass}")
    }
}
