package com.scurab.kuproxy.desktop.screen.main

import com.scurab.kuproxy.KtorConfig
import com.scurab.kuproxy.KtorServer
import com.scurab.kuproxy.ProxyConfig
import com.scurab.kuproxy.ProxyServer
import com.scurab.kuproxy.processor.PassThroughProcessor
import com.scurab.kuproxy.storage.RequestResponse
import com.scurab.ssl.CertificateFactory
import com.scurab.ssl.SslHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
                    state.items.add(it)
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

    fun onDeleteClicked() = with(state) {
        items.clear()
        selectedObject = null
    }

    fun onItemSelected(item: RequestResponse) = with(state) {
        selectedObject = item.takeIf { state.selectedObject != item }
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
}
