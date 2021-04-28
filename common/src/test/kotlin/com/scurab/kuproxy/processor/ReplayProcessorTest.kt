package com.scurab.kuproxy.processor

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.ext.respond
import com.scurab.kuproxy.ext.toDomainRequest
import com.scurab.kuproxy.storage.Repository
import com.scurab.kuproxy.storage.RequestResponse
import io.ktor.client.HttpClient
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import test.junit.SilentLogsExtension
import test.testRequest

@ExtendWith(value = [MockKExtension::class, SilentLogsExtension::class])
internal class ReplayProcessorTest {

    @RelaxedMockK
    lateinit var repo: Repository

    @MockK
    lateinit var client: HttpClient

    @MockK
    lateinit var request: IRequest

    @MockK
    lateinit var response: IResponse

    private lateinit var processor: ReplayProcessor

    @InjectMockKs
    private lateinit var requestResponse: RequestResponse

    @BeforeEach
    fun setUp() {
        processor = spyk(ReplayProcessor(repo, client)) {
            coEvery { send(any(), any()) } returns mockk()
        }
    }

    @Test
    fun `process When request match found in repo Then returns as response`() {
        withTestApplication {
            val testRequest = testRequest()
            coEvery { repo.find(testRequest.toDomainRequest()) } returns requestResponse
            mockkStatic("com.scurab.kuproxy.ext.ApplicationCallKt")
            coEvery { testRequest.respond(any(), any()) } returns mockk()

            runBlocking {
                processor.process(testRequest)
                coEvery { testRequest.respond(any(), any()) } returns mockk()
                coVerify { testRequest.respond(response, any()) }
                coVerify(exactly = 0) { processor.send(any(), any()) }
            }
        }
    }

    @Test
    fun `process When request not found in repo Then sends real request`() {
        withTestApplication {
            val testRequest = testRequest()
            val domainRequest = testRequest.toDomainRequest()

            coEvery { repo.find(domainRequest) } returns null
            mockkStatic("com.scurab.kuproxy.ext.ApplicationCallKt")
            coEvery { processor.send(any(), any()) } returns mockk()

            runBlocking {
                processor.process(testRequest)
                coVerify { processor.send(testRequest, domainRequest) }
                coVerify(exactly = 0) { testRequest.respond(any(), any()) }
            }
        }
    }
}
