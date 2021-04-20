package com.scurab.kuproxy

import com.scurab.kuproxy.properties.ipPort
import java.security.KeyStore
import kotlin.properties.Delegates

interface KtorConfig {
    val httpPort: Int
    val httpsPort: Int
    val keyStore: KeyStore
    val keyStorePassword: CharArray
    val keyAlias: String
    val keyPassword: CharArray

    companion object {
        operator fun invoke(init: KtorConfigBuilder.() -> Unit): KtorConfig = KtorConfigBuilder().apply(init)
    }
}

class KtorConfigBuilder : KtorConfig {
    override var httpPort: Int by ipPort(58880)
    override var httpsPort: Int by ipPort(58888)
    override var keyStore: KeyStore by Delegates.notNull()
    override var keyStorePassword: CharArray = EMPTY_PWD
    override var keyAlias: String = "_KuProxyServer"
    override var keyPassword: CharArray = EMPTY_PWD

    companion object {
        val EMPTY_PWD = CharArray(0)
    }
}
