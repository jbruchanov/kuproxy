package com.scurab.kuproxy.ext

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

internal class BufferedInputStreamKtTest {

    @Test
    fun `readUntil When separator in the middle Then returns 1st part`() {
        val text = "Hello World\nLine1"
        val textRead = ByteArrayInputStream(text.toByteArray())
            .buffered()
            .readUntil(NL)
            .decodeToString()

        assertEquals("Hello World", textRead)
    }

    @Test
    fun `readUntil When separator 1st char Then returns empty`() {
        val text = "\nLine1"
        val textRead = ByteArrayInputStream(text.toByteArray())
            .buffered()
            .readUntil(NL)
            .decodeToString()

        assertEquals("", textRead)
    }

    @Test
    fun `readUntil When separator last char Then returns everything before`() {
        val text = "Line1\n"
        val textRead = ByteArrayInputStream(text.toByteArray())
            .buffered()
            .readUntil(NL)
            .decodeToString()

        assertEquals("Line1", textRead)
    }

    @Test
    fun `readUntil When chained call and keepPosition=true Then expected valued`() {
        val text = "Line1\nLine2\n\nLine4"
        val stream = ByteArrayInputStream(text.toByteArray()).buffered()
        assertEquals("Line1", stream.readUntil(NL, keepPosition = true).decodeToString())
        assertEquals("Line2", stream.readUntil(NL, keepPosition = true).decodeToString())
        assertEquals("", stream.readUntil(NL, keepPosition = true).decodeToString())
        assertEquals("Line4", stream.readUntil(NL, keepPosition = true).decodeToString())
    }

    @Test
    fun `readUntilEmptyLine When Exists Then Steam position moved`() {
        val text = "Line1\r\nLine2\r\n\r\nLine4"
        val inputStream = ByteArrayInputStream(text.toByteArray()).buffered()
        val read = inputStream.readUntilDoubleCrLf(keepPosition = true).decodeToString()
        val rest = inputStream.reader().readText()
        assertEquals("Line1\r\nLine2", read)
        assertEquals("Line4", rest)
    }

    @Test
    fun `readUntilEmptyLine When Exists && keepPosition=true Then Steam position moved`() {
        val text = "Line1\r\nLine2\r\n\r\nLine4"
        val inputStream = ByteArrayInputStream(text.toByteArray()).buffered()
        val read = inputStream.readUntilDoubleCrLf(keepPosition = true).decodeToString()
        val rest = inputStream.reader().readText()
        assertEquals("Line1\r\nLine2", read)
        assertEquals("Line4", rest)
    }

    @Test
    fun `readUntilEmptyLine When Exists && keepPosition=false Then Steam position not moved`() {
        val text = "Line1\r\nLine2\r\n\r\nLine4"
        val inputStream = ByteArrayInputStream(text.toByteArray()).buffered()
        val read = inputStream.readUntilDoubleCrLf(keepPosition = false).decodeToString()
        val rest = inputStream.reader().readText()
        assertEquals("Line1\r\nLine2", read)
        assertEquals(text, rest)
    }

    private val text = "Line1\nLine2\r\n\r\nLine4"

    @Test
    fun `readUntilCrLf When keepPosition=false Then`() {
        val inputStream = ByteArrayInputStream(text.toByteArray()).buffered()
        val read = inputStream.readUntilDoubleCrLf(keepPosition = false)
        assertEquals("Line1\nLine2", read.decodeToString())
        val rest = inputStream.reader().readText()
        assertEquals(text, rest)
    }

    @Test
    fun `readUntilCrLf When keepPosition=true Then`() {
        val inputStream = ByteArrayInputStream(text.toByteArray()).buffered()
        val read = inputStream.readUntilDoubleCrLf(keepPosition = true)
        assertEquals("Line1\nLine2", read.decodeToString())
        val rest = inputStream.reader().readText()
        assertEquals("Line4", rest)
    }

    companion object {
        val NL = "\n".encodeToByteArray()
    }
}
