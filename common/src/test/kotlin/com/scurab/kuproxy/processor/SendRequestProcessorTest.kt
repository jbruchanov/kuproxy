package com.scurab.kuproxy.processor

import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.ext.toDomainRequest
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.content.ByteArrayContent
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondBytes
import io.ktor.server.testing.withTestApplication
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import test.createTestRequest
import test.junit.SilentLogsExtension
import io.ktor.http.Headers as KtorHeaders

@ExtendWith(value = [MockKExtension::class, SilentLogsExtension::class])
class SendRequestProcessorTest {

    fun Application.testCaseSetup() {
        intercept(ApplicationCallPipeline.Call) {
            call.response.headers.append("Header1", "Value1")
            call.response.headers.append("Header2", "Value2a; Value2b")
            call.respondBytes(
                "TestBody".toByteArray(),
                contentType = ContentType.parse("plain/text"),
                HttpStatusCode.Accepted
            )
        }
    }

    private val testReqUrl = "https://www.incoming.com/path/file?queryString=1#asd"
    private val testReqMethod = "PUT"
    private val testReqBody = "TestBody123".toByteArray()
    private val testContentType = "application/test"
    private val testReqHeaders = listOf(
        "Header1" to "Value1",
        "Header2" to "Value2a; Value2b",
        Headers.ContentType to testContentType
    )
    private val testRespBody = "Response123".toByteArray()
    private val testRespHeaders = listOf("Header3" to "Value3")
    private val testRespCode = HttpStatusCode.Accepted

    //TODO: more tests for header Host
    @Test
    fun test() = withTestApplication {
        val testClient = HttpClient(MockEngine) { engine { initRealBackendHandler() } }
        val processor = SendRequestProcessor.Impl(testClient)

        runBlocking {
            val incomingKtorRequest = createTestRequest(testReqUrl, testReqMethod, testReqHeaders, testReqBody)
            val domainRequest = incomingKtorRequest.toDomainRequest()
            application.testCaseSetup()

            //proxied response converted to domain response
            val response = processor.send(incomingKtorRequest, domainRequest)

            assertEquals(testRespCode.value, response.status)
            testRespHeaders.forEach { (k, v) ->
                assertTrue(response.headers.containsKey(k))
                assertEquals(response.headers[k], v)
            }
            assertArrayEquals(testRespBody, response.body)
        }
    }

    private fun MockEngineConfig.initRealBackendHandler() {
        addHandler {
            //proxy -> ktor -> realBackend
            val proxiedRequestFromKtor = it
            //verify we sent everything important from ktor to real backend
            assertEquals(testReqUrl, proxiedRequestFromKtor.url.toString())
            assertEquals(testReqMethod, proxiedRequestFromKtor.method.value)
            testReqHeaders
                .filter { !it.first.equals(Headers.ContentType, ignoreCase = true) }
                .forEach { (k, v) ->
                    assertTrue(proxiedRequestFromKtor.headers.contains(k, v))
                }
            assertEquals(testContentType, proxiedRequestFromKtor.body.contentType?.toString())
            assertArrayEquals(testReqBody, (proxiedRequestFromKtor.body as ByteArrayContent).bytes())

            //realBackend response
            respond(
                content = testRespBody,
                status = testRespCode,
                headers = KtorHeaders.build {
                    testRespHeaders.forEach { (k, v) -> append(k, v) }
                }
            )
        }
    }
}
