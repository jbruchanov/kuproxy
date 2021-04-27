package com.scurab.kuproxy.comm

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

internal class UrlTest {

    @Test
    fun `not equals`() {
        assertNotEquals(Url("http://www.example.com/"), Url("https://www.example.com/"))
        assertNotEquals(Url("https://example.com/"), Url("https://www.example.com/"))
        assertNotEquals(Url("https://www.example.com/"), Url("https://www.example.com"))
        assertNotEquals(Url("https://www.example.com:100/"), Url("https://www.example.com"))
        assertNotEquals(Url("https://www.example.com:100/"), Url("https://www.example.com:101"))
        assertNotEquals(Url("https://www.example.com/?a=b"), Url("https://www.example.com"))
        assertNotEquals(Url("https://www.example.com/?a=b"), Url("https://www.example.com?b=c"))
    }

    @Test
    fun `equals ignores username and password`() {
        assertEquals(
            Url("https://john.doe@www.example.com"),
            Url("https://www.example.com")
        )
        assertEquals(
            Url("https://john.doe:password@www.example.com"),
            Url("https://www.example.com")
        )
        assertEquals(
            Url("https://:password@www.example.com"),
            Url("https://www.example.com")
        )
    }

    @Test
    fun `equals unimportant differences`() {
        //abc.com vs abc.com/ ?
        assertEquals(
            Url("https://www.example.com/?"),
            Url("https://www.example.com/")
        )
        assertEquals(
            Url("https://www.example.com?"),
            Url("https://www.example.com")
        )
        assertEquals(
            Url("https://www.example.com?a=b"),
            Url("https://www.example.com?a=b&")
        )
    }

    @Test
    fun `equals different queryParams order is fine`() {
        assertEquals(
            Url("https://www.example.com/?a=b&c=d"),
            Url("https://www.example.com/?c=d&a=b")
        )
    }

    @Test
    fun `equals fragment is saved`() {
        val uri = "https://www.example.com/?a=b&c=d#test"
        assertEquals("test", Url(uri).fragment)
        assertEquals(uri, Url(uri).toUrlString())
    }
}
