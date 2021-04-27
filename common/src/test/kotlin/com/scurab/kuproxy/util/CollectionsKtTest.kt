package com.scurab.kuproxy.util

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CollectionsKtTest {

    @Test
    fun testMapOfNotNullValues() {
        assertEquals(emptyMap<String, String>(), mapOfNotNullValues<String, String>())
        assertEquals(emptyMap<String, String>(), mapOfNotNullValues("b" to null))
        assertEquals(mapOf("a" to "b"), mapOfNotNullValues("a" to "b", "b" to null))
        assertEquals(mapOf("a" to "b", "b" to "c"), mapOfNotNullValues("a" to "b", "b" to "c"))
    }
}