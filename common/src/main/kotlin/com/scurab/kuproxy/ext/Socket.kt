package com.scurab.kuproxy.ext

import kotlinx.coroutines.CancellationException
import java.net.ServerSocket
import java.net.Socket

fun Socket.closeQuietly() = coRunCatching { if (!isClosed) close() }
fun ServerSocket.closeQuietly() = coRunCatching { if (!isClosed) close() }

inline fun <T, R> T.coRunCatching(block: T.() -> R) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        //swallow
    }
}
