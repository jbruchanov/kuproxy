package com.scurab.kuproxy

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

    override var httpPort: Int by Delegates.observable(58880) { _, _, newValue ->
        require(newValue in (1..65535)) { "Port must be in range of [1 .. 65535], was $newValue" }
    }

    override var httpsPort: Int by Delegates.observable(58888) { _, _, newValue ->
        require(newValue in (1..65535)) { "Port must be in range of [1 .. 65535], was $newValue" }
    }

    override var keyStore: KeyStore by Delegates.notNull()
    override var keyStorePassword: CharArray = EMPTY_PWD
    override var keyAlias: String = "_KuProxyServer"
    override var keyPassword: CharArray = EMPTY_PWD

    companion object {
        val EMPTY_PWD = CharArray(0)
    }
}
