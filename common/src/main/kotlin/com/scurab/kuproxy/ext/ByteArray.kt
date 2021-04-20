package com.scurab.kuproxy.ext

fun ByteArray.indexOf(other: ByteArray): Int {
    if (other.isEmpty()) return -1

    var i = 0
    while (i < size) {
        var j = 0
        while (this[i + j] == other[j]) {
            j++
            if (j >= other.size)
                return i
        }
        i++
    }
    return -1
}
