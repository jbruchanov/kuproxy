package com.scurab.kuproxy.ext

import java.io.InputStream
import java.io.OutputStream

fun InputStream.copyInto(to: OutputStream, bufferSize: Int = 16 * 1024) {
    val buffer = ByteArray(bufferSize)
    kotlin.runCatching {
        var read = this.read(buffer)
        while (read != -1) {
            to.write(buffer, 0, read)
            read = this.read(buffer)
        }
    }
}
