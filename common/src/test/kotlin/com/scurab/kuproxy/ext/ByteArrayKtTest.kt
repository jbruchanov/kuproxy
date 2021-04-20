package com.scurab.kuproxy.ext

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ByteArrayKtTest {

    private val toFind = "ab".toByteArray()

    @ParameterizedTest(name = "indexOf When {0} Then {1}")
    @CsvSource(value = ["ab.,0", ".ab.,1", "...ab,3", "...,-1"])
    fun indexOf(text: String, expected: Int) {
        assertEquals(expected, text.toByteArray().indexOf(toFind))
    }

    @Test
    fun `indexOf When toFind Empty Then -1`() {
        assertEquals(-1, "123".toByteArray().indexOf(ByteArray(0)))
    }

    @Test
    fun `indexOf When data Empty Then -1`() {
        assertEquals(-1, "".toByteArray().indexOf(toFind))
    }
}
