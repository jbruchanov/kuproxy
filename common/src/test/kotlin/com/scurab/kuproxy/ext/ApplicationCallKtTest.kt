package com.scurab.kuproxy.ext

import com.scurab.kuproxy.comm.Url
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import test.junit.SilentLogsExtension
import test.request
import test.testRequest

@ExtendWith(SilentLogsExtension::class)
internal class ApplicationCallKtTest {

    @Test
    fun toDomainRequest() {
        withTestApplication {
            val request = testRequest(
                method = "DELETE",
                headers = listOf(
                    "Header1" to "a",
                    "Header1" to "b",
                    "Header2" to "c",
                )
            )
            val domainRequest = request.toDomainRequest()
            val expectedRequest = request {
                url = Url("http://www.test.com")
                method = "DELETE"
                headers = mapOf(
                    "Header1" to "a; b",
                    "Header2" to "c",
                )
            }
            assertEquals(expectedRequest.url, domainRequest.url)
            assertEquals(expectedRequest.method, domainRequest.method)
            assertEquals(expectedRequest.headers, domainRequest.headers)
        }
    }
}
