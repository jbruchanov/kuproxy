package com.scurab.kuproxy

import com.scurab.kuproxy.matcher.DefaultRequestMatcher
import com.scurab.kuproxy.matcher.ExactRequestMatcher
import com.scurab.kuproxy.model.Tape
import com.scurab.kuproxy.processor.PassThroughProcessor
import com.scurab.kuproxy.processor.RecordingProcessor
import com.scurab.kuproxy.processor.ReplayProcessor
import com.scurab.kuproxy.processor.RequestToStoreProcessor
import com.scurab.kuproxy.serialisation.TapeExportConverter
import com.scurab.kuproxy.serialisation.TapeImportConverter
import com.scurab.kuproxy.storage.MemRepository
import com.scurab.kuproxy.storage.RequestResponse
import com.scurab.ssl.CertificateFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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

@ExtendWith(SilentLogsExtension::class)
internal class ProxyServerTest {

    val domains = listOf("localhost", "zunpa.cz", "scurab.com", "*.scurab.com", "cdr.cz", "*.cdr.cz")
    val domains = listOf("api.pit.vhi.ie")
    val domainsRegexps = domains.map {
        ("\\Q$it\\E").replace("*", "\\E.*\\Q").toRegex()
    }

    val savingChannel = Channel<Unit>()

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

        val mode = Mode.Replay

        val processor = when (mode) {
            Mode.Passthrough -> PassThroughProcessor()
            Mode.Replay -> {
                @Suppress("UNCHECKED_CAST")
                val tape = File("saved.yaml").inputStream()
                    .use { Yaml().loadAs(it, Map::class.java) as Map<String, Any> }
                    .let { TapeImportConverter().convert(it) }
                ReplayProcessor(MemRepository(tape, DefaultRequestMatcher()), client)
            }
            Mode.Record -> {
                val prc = RequestToStoreProcessor()
                val repo = object : MemRepository(ExactRequestMatcher(), prc) {
                    override suspend fun add(item: RequestResponse) {
                        println("Saving:${item.request.url}")
                        val toStore = prc.process(item)
                        val found = find(toStore.request)
                        if (found != null) {
                            println("Replacing:${item.request.url}")
                            remove(found)
                        }
                        println("Saved:${item.request.url}")
                        super.add(toStore)
                        savingChannel.send(Unit)
                    }
                }
                val converter = TapeExportConverter()
                GlobalScope.launch {
                    savingChannel.consumeAsFlow()
                        .debounce(1000)
                        .flowOn(Dispatchers.IO)
                        .collect {
                            val converted = converter.convert(Tape("test", repo.items))
                            val dump = Yaml().dumpAs(converted, Tag("tape"), DumperOptions.FlowStyle.BLOCK)
                            File("saved.yaml").outputStream().writer().use {
                                it.write(dump)
                            }
                        }
                }
                RecordingProcessor(repo, client)
            }
        }
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
