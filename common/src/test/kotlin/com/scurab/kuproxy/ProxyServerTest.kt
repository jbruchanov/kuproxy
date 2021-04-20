package com.scurab.kuproxy

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import test.SslHelper

internal class ProxyServerTest {

    @Test
    @Disabled("manual")
    fun test() = runBlocking {
        val ktorConfig = KtorConfig {
            keyStore = SslHelper.createServerCertSignedByCA()
            keyAlias = SslHelper.ServerAlias
        }
        KtorServer(ktorConfig).start()
        val proxyConfig = ProxyConfig {
            httpServerPort = ktorConfig.httpPort
            httpServerPort = ktorConfig.httpsPort
        }
        ProxyServer(proxyConfig).start()

        delay(120000 * 20)
    }
}
