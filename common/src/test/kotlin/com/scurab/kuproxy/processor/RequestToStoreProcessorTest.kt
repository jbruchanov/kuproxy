package com.scurab.kuproxy.processor

import com.scurab.kuproxy.storage.RequestResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import test.testDomainRequest
import test.testDomainResponse

internal class RequestToStoreProcessorTest {

    @Test
    fun testNoHeadersInRequest() = runBlocking {
        val processor = RequestToStoreProcessor()
        val requestResponse = RequestResponse(
            testDomainRequest(),
            testDomainResponse()
        )
        val item = processor.process(requestResponse)

        assertEquals(testDomainRequest(headers = emptyMap()), item.request)
        assertTrue(requestResponse.response === item.response)
    }

    @Test
    fun testSpecifiedHeadersInRequestOnly() = runBlocking {
        val processor = RequestToStoreProcessor { header ->
            header.equals("h1", ignoreCase = true)
        }
        val requestResponse = RequestResponse(
            testDomainRequest(headers = mapOf("h1" to "v1", "h2" to "v2")),
            testDomainResponse()
        )
        val item = processor.process(requestResponse)

        assertEquals(testDomainRequest(headers = mapOf("h1" to "v1")), item.request)
        assertTrue(requestResponse.response === item.response)
    }
}
