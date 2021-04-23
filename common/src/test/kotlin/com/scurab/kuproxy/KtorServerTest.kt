package com.scurab.kuproxy

import com.scurab.ssl.CertificateFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import test.SslHelper

internal class KtorServerTest {

    @Test
    @Disabled("manual")
    fun testKtorServer() = runBlocking {
        // turn off any logging
        // (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.OFF

        val serverCertsKeyStore =
            SslHelper.createServerCertSignedByCA(CertificateFactory.embeddedCACertificate, listOf("localhost"))
        val config = KtorConfig {
            httpPort = 8080
            httpsPort = 8088
            keyStore = serverCertsKeyStore
            keyAlias = SslHelper.ServerAlias
        }

        KtorServer(config).start()
        delay(120000 * 20)
    }
}
