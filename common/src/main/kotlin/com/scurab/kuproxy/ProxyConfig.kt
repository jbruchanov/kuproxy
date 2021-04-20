package com.scurab.kuproxy

import com.scurab.kuproxy.properties.ipPort

interface ProxyConfig {
    val port: Int
    val httpServerPort: Int
    val httpsServerPort: Int
    val domains: List<Regex>

    companion object {
        operator fun invoke(block: ProxyConfigBuilder.() -> Unit): ProxyConfig {
            return ProxyConfigBuilder().apply(block)
        }
    }
}

class ProxyConfigBuilder : ProxyConfig {
    override var port: Int by ipPort(8888)
    override var httpServerPort: Int by ipPort(8888)
    override var httpsServerPort: Int by ipPort(8888)
    override var domains: List<Regex> = emptyList()
}
