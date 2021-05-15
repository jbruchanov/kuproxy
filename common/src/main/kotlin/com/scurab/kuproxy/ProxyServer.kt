package com.scurab.kuproxy

import com.scurab.kuproxy.ext.closeQuietly
import com.scurab.kuproxy.ext.copyInto
import com.scurab.kuproxy.ext.readUntil
import com.scurab.kuproxy.ext.readUntilDoubleCrLf
import com.scurab.kuproxy.ext.toDomainRegex
import com.scurab.kuproxy.util.AnsiEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ServerSocketFactory

class ProxyServer(private val config: ProxyConfig) {

    private var socket: ServerSocket? = null
    private var job: Job? = null
    private var isActive = false
    private val workingThreads = AtomicInteger()
    private val domains = config.domains.map { it.toDomainRegex() }

    fun start() {
        if (socket != null) {
            return
        }

        isActive = true
        job = GlobalScope.launch(Dispatchers.IO) {
            val socket = ServerSocketFactory.getDefault().createServerSocket(config.port).also {
                this@ProxyServer.socket = it
            }
            while (this@ProxyServer.isActive) {
                try {
                    val connectedSocket = socket.accept()
                    launch(Dispatchers.IO) {
                        try {
                            handleSocket(connectedSocket)
                        } catch (t: Throwable) {
                            connectedSocket.closeQuietly()
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun stop() {
        isActive = false
        socket?.closeQuietly()
        runBlocking {
            //TODO: something better
            println("ProxyServer:cancelAndJoin")
            job?.cancelAndJoin()
        }
        socket = null
        workingThreads.set(0)
    }

    private suspend fun handleSocket(clientSocket: Socket) {
        val bufferedInputStream = BufferedInputStream(clientSocket.getInputStream())
        val line = bufferedInputStream
            .readUntil(CRLF)
            .decodeToString()

        when {
            line.isBlank() -> clientSocket.closeQuietly()
            else -> onRequest(clientSocket, ConnectDef.parse(line), bufferedInputStream)
        }
    }

    private suspend fun onRequest(clientSocket: Socket, def: ConnectDef, istream: BufferedInputStream) {
        var serverSocket: Socket? = null
        try {
            serverSocket = createSocket(def)
        } catch (ex: UnknownHostException) {
            clientSocket.respond(ERR_UNKNOWN_IP)
        } catch (ex: SocketTimeoutException) {
            clientSocket.respond(ERR_TIMEOUT)
        } catch (t: Throwable) {
            clientSocket.respond(response(500, t.javaClass.name))
        }

        if (serverSocket == null) {
            clientSocket.closeQuietly()
            return
        }

        val isProxying = serverSocket.inetAddress.isLoopbackAddress
        val log = when {
            isProxying && def.isSsl -> AnsiEffect.TextYellow(def.toLogString())
            isProxying -> AnsiEffect.TextGreen(def.toLogString())
            else -> AnsiEffect.TextDarkGray(def.toLogString())
        }
        println(log)

        if (def.isSsl) {
            istream.readUntilDoubleCrLf(keepPosition = true)
            with(clientSocket.getOutputStream()) {
                write(SSL_OPENED)
                flush()
            }
        }

        coroutineScope {
            jobTask {
                istream.copyInto(serverSocket.getOutputStream())
            }

            jobTask {
                serverSocket.getInputStream().copyInto(clientSocket.getOutputStream())
                clientSocket.closeQuietly()
                serverSocket.closeQuietly()
            }
        }
    }

    private fun createSocket(def: ConnectDef): Socket {
        val isProcessing = domains.find { regexp -> def.url.matches(regexp) } != null

        val url = if (isProcessing) LOCALHOST else def.url
        val port = when {
            def.isSsl && isProcessing -> config.httpsServerPort
            !def.isSsl && isProcessing -> config.httpServerPort
            else -> def.port
        }
        return Socket().also {
            it.connect(InetSocketAddress(url, port), 2000)
            it.soTimeout = 2000
        }
    }

    private fun CoroutineScope.jobTask(block: suspend () -> Unit) {
        launch(Dispatchers.IO) {
            workingThreads.incrementAndGet()
            try {
                block()
            } finally {
                workingThreads.decrementAndGet()
            }
        }
    }

    private fun Socket.respond(byteArray: ByteArray) {
        kotlin.runCatching {
            with(getOutputStream()) {
                write(byteArray)
                flush()
            }
        }
    }

    companion object {
        private const val LOCALHOST = "localhost"
        private val CRLF = "\r\n".toByteArray()
        private val SSL_OPENED = response(200, "Connection established")
        private val ERR_TIMEOUT = response(408, "Request timeout")
        private val ERR_UNKNOWN_IP = response(502, "unknown ip address")

        private fun response(code: Int, message: String) = (
            "HTTP/1.0 $code ${message}\r\n" +
                "Proxy-Agent: ProxyServer/1.0\r\n" +
                "\r\n"
            ).toByteArray()
    }
}
