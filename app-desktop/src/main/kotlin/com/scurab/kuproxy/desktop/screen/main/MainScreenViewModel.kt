package com.scurab.kuproxy.desktop.screen.main

import com.scurab.kuproxy.KtorConfig
import com.scurab.kuproxy.KtorServer
import com.scurab.kuproxy.ProxyConfig
import com.scurab.kuproxy.ProxyServer
import com.scurab.kuproxy.desktop.ext.ans
import com.scurab.kuproxy.desktop.ext.mapCatching
import com.scurab.kuproxy.processor.PassThroughProcessor
import com.scurab.kuproxy.serialisation.TapeImportConverter
import com.scurab.ssl.CertificateFactory
import com.scurab.ssl.SslHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.yaml.snakeyaml.Yaml
import java.io.File

class MainScreenViewModel {
    val state = MainWindowState()
    var config: ProxyConfig = ProxyConfig {
        domains = listOf("cdr.cz", "*.cdr.cz")
    }
        private set

    private var ktorServer: KtorServer? = null
    private var proxyServer: ProxyServer? = null

    fun start() {
        stop()

        val serverCertsKeyStore = SslHelper.createServerCertSignedByCA(
            CertificateFactory.embeddedCACertificate,
            config.domains
        )

        val ktorConfig = KtorConfig {
            keyStore = serverCertsKeyStore
            keyAlias = SslHelper.ServerAlias
        }

        val processor = PassThroughProcessor().apply {
            callback = {
                synchronized(state) {
                    state.proxyTabState.items.add(it)
                }
            }
        }

        ktorServer = KtorServer(ktorConfig, processor).also { it.start() }
        val proxyConfig = ProxyConfig {
            httpServerPort = ktorConfig.httpPort
            httpsServerPort = ktorConfig.httpsPort
            this.domains = config.domains
        }
        proxyServer = ProxyServer(proxyConfig).also { it.start() }
    }

    fun stop() {
        proxyServer?.stop()
        ktorServer?.stop()
        proxyServer = null
        ktorServer = null
    }

    fun onDeleteClicked() = with(state.proxyTabState) {
        items.clear()
        selectedRowIndex = -1
    }

    fun onItemSelected(index: Int) = with(state.currentTabState) {
        selectedRowIndex = index.inSelectedTabItemsRange()
    }

    fun onSettingsClicked() = with(state) {
        isConfigVisible = true
    }

    fun onConfigSaved(config: ProxyConfig) {
        state.isConfigVisible = false
        this.config = config
        //TODO:something better, skip restart if nothing changed
        GlobalScope.launch {
            start()
        }
    }

    fun onTabClosed(tab: TabItem) = with(state) {
        state.tabs.remove(tab)
        if (tab == selectedTab) {
            selectedTab = state.tabs.first()
        }
    }

    fun onTabClicked(tab: TabItem) {
        state.selectedTab = tab
    }

    fun onTabChecked(tab: TabItem) {
        state.checkedTab = tab
    }

    private var newTabCounter = 1

    fun addNewTab() {
        val newTab = TabItem("New-$newTabCounter".ans, closable = true, checkable = true, TabState())
        state.tabs.add(newTab)
        if (state.checkedTab == null) {
            state.checkedTab = newTab
        }
        newTabCounter++
    }

    fun onModeChanged(mode: Mode) {
        state.mode = mode
        state.modeDropDownMenuExpanded = false
    }

    private fun Int.inItemsRange() = coerceAtLeast(0).coerceAtMost(state.proxyTabState.items.size - 1)
    private fun Int.inSelectedTabItemsRange() = coerceAtLeast(0).coerceAtMost(state.currentTabState.items.size - 1)

    fun onKeepScrolledBottomClicked() {
        state.keepScrolledBottom = !state.keepScrolledBottom
    }

    //TODO: DI
    private val yaml = Yaml()
    private val converter = TapeImportConverter()

    fun onLoadFiles(files: List<File>) {
        GlobalScope.launch {
            files
                .asSequence()
                .filter { it.name.endsWith(".yaml") }
                .mapCatching { it.inputStream().use { istream -> yaml.loadAs(istream, Map::class.java) as Map<String, Any> } }
                .mapCatching { converter.convert(it) }
                .let { tapes ->
                    withContext(Dispatchers.Main) {
                        tapes.forEach { tape ->
                            state.tabs.add(TabItem(tape.name.ans, closable = true, checkable = true, TabState(tape.interactions)))
                        }
                    }
                }
        }
    }
}
