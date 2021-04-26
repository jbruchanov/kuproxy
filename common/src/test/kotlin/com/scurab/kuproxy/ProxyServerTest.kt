package com.scurab.kuproxy

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.scurab.kuproxy.processor.PassThroughProcessor
import com.scurab.ssl.CertificateFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import test.SslHelper

internal class ProxyServerTest {

    val domains = listOf("localhost", "zunpa.cz", "scurab.com", "*.scurab.com", "cdr.cz", "*.cdr.cz")
    val domainsRegexps = domains.map {
        ("\\Q$it\\E").replace("*", "\\E.*\\Q").toRegex()
    }

    @Test
    @Disabled("manual")
    fun test() = runBlocking {
        (LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger).level = Level.OFF

        val serverCertsKeyStore =
            SslHelper.createServerCertSignedByCA(CertificateFactory.embeddedCACertificate, domains)

        val ktorConfig = KtorConfig {
            keyStore = serverCertsKeyStore
            keyAlias = SslHelper.ServerAlias
        }

        val client = HttpClient(CIO) { expectSuccess = false }
        val processor = PassThroughProcessor(client)

        KtorServer(ktorConfig, processor).start()
        val proxyConfig = ProxyConfig {
            httpServerPort = ktorConfig.httpPort
            httpsServerPort = ktorConfig.httpsPort
            domains = domainsRegexps
        }
        ProxyServer(proxyConfig).start()

        delay(120000 * 20)
    }
}
