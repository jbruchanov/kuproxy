package com.scurab.kuproxy.properties

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class IpPortPropertyTest {

    private val sample = Sample()

    @Test
    fun `get When default value Then default value`() {
        assertEquals(10, sample.port)
    }

    @Test
    fun `get When set new value Then get new value`() {
        sample.port = 1024
        assertEquals(1024, sample.port)
    }

    @Test
    fun `get When set new value outside range Then IllegalArgumentException`() {
        assertThrows(IllegalArgumentException::class.java) {
            sample.port = 0
        }
        assertThrows(IllegalArgumentException::class.java) {
            sample.port = 65536
        }
    }

    private class Sample {
        var port by ipPort(10)
    }
}
