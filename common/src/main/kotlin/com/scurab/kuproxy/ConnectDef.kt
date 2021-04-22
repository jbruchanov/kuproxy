package com.scurab.kuproxy

import com.scurab.kuproxy.util.AnsiEffect
import java.net.URI

data class ConnectDef(val method: String, val url: String, val port: Int, val httpVersion: String) {
    val isSsl = method == CONNECT

    fun toLogString(): String {
        return if (isSsl) {
            AnsiEffect.TextYellow("SSL $url:$port")
        } else {
            AnsiEffect.TextGray("TXT $url:$port")
        }
    }

    companion object {
        const val CONNECT = "CONNECT"

        fun parse(methodDef: String): ConnectDef {
            val elems = methodDef.split(" ")
            val method = elems[0]
            val httpVersion = elems[2]
            return if (CONNECT == method) {
                val urlParts = elems[1].split(":")
                ConnectDef(method, urlParts[0], urlParts[1].toInt(), httpVersion)
            } else {
                val uri = URI.create(elems[1])
                ConnectDef(method, uri.host, uri.port.takeIf { it != -1 } ?: 80, httpVersion)
            }
        }
    }
}
