package com.scurab.kuproxy.processor

import com.scurab.kuproxy.ext.toDomainRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import test.junit.SilentLogsExtension
import test.testRequest

@ExtendWith(SilentLogsExtension::class)
internal class PassThroughProcessorTest {

    @Test
    fun `process calls send`() {
        val processor = spyk(PassThroughProcessor()) {
            coEvery { send(any(), any()) } returns mockk()
        }

        withTestApplication {
            runBlocking {
                val request = testRequest()
                processor.process(request)
                val domainRequest = request.toDomainRequest()
                coVerify { processor.send(request, domainRequest) }
            }
        }
    }
}
