package com.scurab.kuproxy.ext

import java.io.BufferedInputStream

private val CRLF = "\r\n".repeat(2).toByteArray()
private val EMPTY_ARRAY = ByteArray(0)

fun BufferedInputStream.readUntilDoubleCrLf(keepPosition: Boolean = false, bufferSize: Int = DEFAULT_BUFFER_SIZE) =
    readUntil(CRLF, keepPosition, bufferSize)

/**
 * Read stream until specific data found
 * @param byteArray - data to find
 * @param keepPosition - false to reset the stream position
 * @param lenToSearchFor -
 */
fun BufferedInputStream.readUntil(
    byteArray: ByteArray,
    keepPosition: Boolean = false,
    lenToSearchFor: Int = DEFAULT_BUFFER_SIZE
): ByteArray {
    mark(lenToSearchFor)
    val buffer = ByteArray(lenToSearchFor)
    val read = read(buffer)
    return if (read != -1) {
        val indexOf = buffer.indexOf(byteArray)
        val len = if (indexOf > -1) {
            reset()
            if (keepPosition) {
                skip(indexOf.toLong() + byteArray.size)
            }
            indexOf
        } else {
            read
        }
        buffer.sliceArray(0 until len)
    } else {
        EMPTY_ARRAY
    }
}
