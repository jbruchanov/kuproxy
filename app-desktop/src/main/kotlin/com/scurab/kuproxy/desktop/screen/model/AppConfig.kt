package com.scurab.kuproxy.desktop.screen.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.scurab.kuproxy.ProxyConfig
import com.scurab.kuproxy.ext.toPortNumber

class AppConfig {
    var port by mutableStateOf("")
    var domains by mutableStateOf("")

    fun toConfig(): ProxyConfig = ProxyConfig {
        this.port = this@AppConfig.port.toPortNumber()
        this.domains = this@AppConfig.domains.split("\n").filter { it.isNotEmpty() }
    }

    companion object {
        fun fromProxyConfig(proxyConfig: ProxyConfig) = AppConfig().apply {
            port = proxyConfig.port.toString()
            domains = proxyConfig.domains.joinToString("\n")
        }
    }
}
