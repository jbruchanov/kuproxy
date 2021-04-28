package com.scurab.kuproxy.processor

import com.scurab.kuproxy.comm.IResponse
import com.scurab.kuproxy.ext.toDomainRequest
import com.scurab.kuproxy.storage.Repository
import com.scurab.kuproxy.storage.RequestResponse
import io.ktor.client.HttpClient
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import test.junit.SilentLogsExtension
import test.testRequest

@ExtendWith(value = [MockKExtension::class, SilentLogsExtension::class])
internal class RecordingProcessorTest {

    @RelaxedMockK
    lateinit var repo: Repository

    @MockK
    lateinit var client: HttpClient

    @MockK
    lateinit var response: IResponse

    private lateinit var processor: RecordingProcessor

    @BeforeEach
    fun setUp() {
        processor = spyk(RecordingProcessor(repo, client))
    }

    @Test
    fun `process saves RequestResponse to repo`() {
        withTestApplication {
            val request = testRequest()
            val domainRequest = request.toDomainRequest()

            coEvery { processor.send(request, domainRequest) } returns response

            runBlocking {
                processor.process(request)
                coVerify { processor.send(request, domainRequest) }

                val slot = slot<RequestResponse>()
                coVerify { repo.add(capture(slot)) }

                assertEquals(domainRequest, slot.captured.request)
                assertEquals(response, slot.captured.response)
            }
        }
    }
}
