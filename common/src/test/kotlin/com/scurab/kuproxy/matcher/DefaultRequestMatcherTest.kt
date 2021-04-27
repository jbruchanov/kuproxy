package com.scurab.kuproxy.matcher

import com.scurab.kuproxy.comm.IRequest
import com.scurab.kuproxy.comm.Url
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import test.request

internal class DefaultRequestMatcherTest {

    private val matcher = DefaultRequestMatcher()

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(Args::class)
    fun match(@Suppress("UNUSED_PARAMETER") name: String, equals: Boolean, real: IRequest, stored: IRequest) {
        Assertions.assertEquals(equals, matcher.isMatching(real, stored))
    }

    class Args : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) = listOf(
            Arguments.of(
                "Same request is matching", true,
                request {
                    url = Url("http://www.test.com/test?a=b&b=c")
                    method = GET
                },
                request {
                    url = Url("http://www.test.com/test?b=c&a=b&")
                    method = GET
                }
            ),
            Arguments.of(
                "Different methods is NOT matching", false,
                request {
                    url = Url("http://www.test.com")
                    method = POST
                },
                request {
                    url = Url("http://www.test.com")
                    method = GET
                }
            ),
            Arguments.of(
                "Matching headers, more values in real request is fine", true,
                request {
                    url = Url("http://www.test.com")
                    method = GET
                    headers = mapOf(ContentType to setOf("a", "b"), AcceptEncoding to setOf("c"))
                },
                request {
                    url = Url("http://www.test.com")
                    method = GET
                    headers = mapOf(ContentType to setOf("a", "b"))
                }
            ),
            Arguments.of(
                "Matching headers, no stored headers, matches anything", true,
                request {
                    url = Url("http://www.test.com")
                    method = GET
                    headers = mapOf(ContentType to setOf("a", "b"), AcceptEncoding to setOf("c"))
                },
                request {
                    url = Url("http://www.test.com")
                    method = GET
                }
            ),
            Arguments.of(
                "Matching headers, different values are NOT matching", false,
                request {
                    url = Url("http://www.test.com")
                    method = GET
                    headers = mapOf(ContentType to setOf("a", "b"), AcceptEncoding to setOf("c"))
                },
                request {
                    url = Url("http://www.test.com")
                    method = GET
                    headers = mapOf(ContentType to setOf("a", "c"), AcceptEncoding to setOf("b"))
                }
            )
        ).stream()
    }

    companion object {
        const val GET = "GET"
        const val POST = "POST"
        const val ContentType = "Content-Type"
        const val AcceptEncoding = "Accept-Encoding"
    }
}