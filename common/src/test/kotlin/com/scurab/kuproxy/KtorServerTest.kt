package com.scurab.kuproxy

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import test.SslHelper

internal class KtorServerTest {

    @Test
    @Disabled("manual")
    fun testKtorServer() {
        // turn off any logging
        // (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.OFF

        val config = KtorConfig {
            httpPort = 8080
            httpsPort = 8088
            keyStore = SslHelper.createServerCertSignedByCA()
            keyAlias = SslHelper.ServerAlias
        }

        KtorServer(config).also {
            it.start()
            runBlocking {
                delay(60000)
                it.stop()
            }
        }
    }
}
