package com.scurab.kuproxy

import com.scurab.kuproxy.ext.proxy
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Internal server for http processing
 */
class KtorServer(private val ktorConfig: KtorConfig) {

    private var server: ApplicationEngine? = null

    val isRunning get() = server != null

    val client = HttpClient(CIO) {
        expectSuccess = false
    }

    fun start() {
        if (server != null) return

        server = embeddedServer(
            Netty,
            applicationEngineEnvironment {
                connector {
                    port = ktorConfig.httpPort
                }

                sslConnector(
                    ktorConfig.keyStore,
                    ktorConfig.keyAlias,
                    { ktorConfig.keyStorePassword },
                    { ktorConfig.keyPassword }
                ) {
                    port = ktorConfig.httpsPort
                }

                module {
                    intercept(ApplicationCallPipeline.Call) {
                        withContext(Dispatchers.IO) {
                            call.proxy(client)
                        }
                    }
                }
            }
        ).also {
            it.start(wait = false)
        }
    }

    fun stop(timeout: Long = 4000) {
        server?.stop(timeout / 2, timeout / 2)
        server = null
    }
}
