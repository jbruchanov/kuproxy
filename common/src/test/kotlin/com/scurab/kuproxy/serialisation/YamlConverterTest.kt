package com.scurab.kuproxy.serialisation

import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.model.Tape
import com.scurab.kuproxy.storage.RequestResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import test.testDomainRequest
import test.testDomainResponse
import java.util.Date

internal class YamlConverterTest {

    @Suppress("DEPRECATION")
    private val tape = Tape(
        "test",
        interactions = listOf(
            RequestResponse(
                testDomainRequest(
                    method = "GET",
                    headers = mapOf("K1" to "V1", "K2" to "V2"),
                    recorded = Date(2021 - 1900, 3, 1, 12, 0, 0).time
                ),
                testDomainResponse(
                    body =
                        """
                            <html>
                                <body>
                                    Test
                                </body>
                            </html>
                        """.trimIndent().toByteArray()
                )
            ),
            RequestResponse(
                testDomainRequest(recorded = Date(2021 - 1900, 1, 2, 8, 15, 25).time),
                testDomainResponse(
                    headers = mapOf("TestResHeader" to "TestResValue", Headers.ContentType to "application/octet-stream"),
                    body = byteArrayOf(1, 2, 3)
                )
            ),
            RequestResponse(
                testDomainRequest(recorded = Date(2021 - 1900, 2, 2, 22, 30, 45).time),
                testDomainResponse(body = ByteArray(0))
            )
        )
    )

    @Test
    fun testSerialise() {
        val converter = TapeExportConverter()
        val converted = converter.convert(tape)
        val dump = Yaml().dumpAs(converted, Tag("tape"), DumperOptions.FlowStyle.BLOCK)
        assertEquals(expected, dump)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testDeserialize() {
        val parse = Yaml().loadAs(expected, Map::class.java) as Map<String, Any>
        val parsed = TapeImportConverter().convert(parse)

        assertEquals(tape.name, parsed.name)
        assertEquals(tape.interactions.size, parsed.interactions.size)
        tape.interactions.indices.forEach { i ->
            val (req, resp) = tape.interactions[i]
            val (parsedReq, parsedResp) = parsed.interactions[i]
            assertEquals(req, parsedReq)
            assertEquals(resp, parsedResp, "Interaction[$i]")
        }
    }

    val expected =
        """
        !<tape>
        name: test
        interactions:
        - request:
            url: http://www.test.com
            method: GET
            headers:
              K1: V1
              K2: V2
            recorded: '2021-04-01 12:00:00'
          response:
            status: 202
            headers:
              TestResHeader: TestResValue
              content-type: text/plain
            body: |-
              <html>
                  <body>
                      Test
                  </body>
              </html>
        - request:
            url: http://www.test.com
            method: POST
            headers:
              TestReqHeader: TestReqValue
            recorded: '2021-02-02 08:15:25'
          response:
            status: 202
            headers:
              TestResHeader: TestResValue
              content-type: application/octet-stream
            body: !!binary |-
              AQID
        - request:
            url: http://www.test.com
            method: POST
            headers:
              TestReqHeader: TestReqValue
            recorded: '2021-03-02 22:30:45'
          response:
            status: 202
            headers:
              TestResHeader: TestResValue
              content-type: text/plain
        """.trimIndent() + "\n"
}
