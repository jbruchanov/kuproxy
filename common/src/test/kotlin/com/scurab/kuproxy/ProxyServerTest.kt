package com.scurab.kuproxy

import com.scurab.kuproxy.matcher.DefaultRequestMatcher
import com.scurab.kuproxy.model.Tape
import com.scurab.kuproxy.processor.PassThroughProcessor
import com.scurab.kuproxy.processor.RecordingProcessor
import com.scurab.kuproxy.processor.ReplayProcessor
import com.scurab.kuproxy.serialisation.TapeExportConverter
import com.scurab.kuproxy.serialisation.TapeImportConverter
import com.scurab.kuproxy.storage.MemRepository
import com.scurab.kuproxy.storage.RequestResponse
import com.scurab.ssl.CertificateFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import test.SslHelper
import test.junit.SilentLogsExtension
import java.io.File
import java.util.concurrent.Executors

@ExtendWith(SilentLogsExtension::class)
internal class ProxyServerTest {

    val domains = listOf("localhost", "zunpa.cz", "scurab.com", "*.scurab.com", "www.cdr.cz", "*.cdr.cz", "cdr.cz")

    enum class Mode {
        Passthrough, Record, Replay
    }

    @Test
    @Disabled("manual")
    fun test() = runBlocking {
        val serverCertsKeyStore =
            SslHelper.createServerCertSignedByCA(CertificateFactory.embeddedCACertificate, domains)

        val ktorConfig = KtorConfig {
            keyStore = serverCertsKeyStore
            keyAlias = SslHelper.ServerAlias
        }

        val client = HttpClient(Apache) {
            expectSuccess = false
        }

        val mode = Mode.Passthrough
        val processor = when (mode) {
            Mode.Passthrough -> PassThroughProcessor()
            Mode.Record -> {
                val savingDispatches = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
                val repo = object : MemRepository(DefaultRequestMatcher()) {
                    override suspend fun add(item: RequestResponse) {
                        super.add(item)
                        GlobalScope.async(savingDispatches) {
                            val converter = TapeExportConverter()
                            val converted = converter.convert(Tape("test", items))
                            val dump = Yaml().dumpAs(converted, Tag("tape"), DumperOptions.FlowStyle.BLOCK)
                            File("saved.yaml").outputStream().writer().use {
                                it.write(dump)
                            }
                        }
                    }
                }
                RecordingProcessor(repo, client)
            }
            Mode.Replay -> {
                @Suppress("UNCHECKED_CAST")
                val tape = File("saved.yaml").inputStream()
                    .use { Yaml().loadAs(it, Map::class.java) as Map<String, Any> }
                    .let { TapeImportConverter().convert(it) }
                ReplayProcessor(MemRepository(tape, DefaultRequestMatcher()), client)
            }
        }

        KtorServer(ktorConfig, processor).start()
        val proxyConfig = ProxyConfig {
            httpServerPort = ktorConfig.httpPort
            httpsServerPort = ktorConfig.httpsPort
            domains = this@ProxyServerTest.domains
        }
        ProxyServer(proxyConfig).start()

        delay(120000 * 20)
    }
}
