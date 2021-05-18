package test

import com.scurab.kuproxy.comm.DomainHeaders
import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.Request
import com.scurab.kuproxy.comm.Response
import com.scurab.kuproxy.comm.Url
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.setBody

fun request(block: RequestBuilder.() -> Unit): IRequest = RequestBuilder().apply(block)

fun testDomainRequest(
    uri: String = "http://www.test.com",
    method: String = "POST",
    headers: DomainHeaders = mapOf("TestReqHeader" to "TestReqValue"),
    recorded: Long = System.currentTimeMillis()
) = Request(Url(uri), method, headers, recorded)

fun testDomainResponse(
    status: Int = 202,
    headers: DomainHeaders = mapOf("TestResHeader" to "TestResValue", Headers.ContentType to "text/plain"),
    body: ByteArray = "TestBody".toByteArray()
) = Response(status, headers, body)

class RequestBuilder : IRequest {
    override lateinit var url: Url
    override var method: String = "GET"
    override var headers: DomainHeaders = emptyMap()
    override var recorded: Long = System.currentTimeMillis()
    override val body: ByteArray = ByteArray(0)
}

fun TestApplicationEngine.testRequest(
    uri: String = "http://www.test.com",
    method: String = "POST",
    headers: List<Pair<String, String>> = listOf("TestHeader" to "TestValue"),
    body: ByteArray = ByteArray(0)
): TestApplicationCall {
    return handleRequest {
        this.uri = uri
        this.method = HttpMethod(method)
        headers.forEach { (k, v) ->
            addHeader(k, v)
        }
        setBody(body)
    }
}

fun TestApplicationEngine.createTestRequest(
    uri: String = "http://www.test.com",
    method: String = "POST",
    headers: List<Pair<String, String>> = listOf("TestHeader" to "TestValue"),
    body: ByteArray = ByteArray(0)
): TestApplicationCall {
    return createCall {
        headers.forEach { (k, v) ->
            this.addHeader(k, v)
        }
        this.method = HttpMethod(method)
        this.uri = uri
        this.setBody(body)
    }
}
