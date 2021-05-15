package com.scurab.kuproxy.desktop.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun <I> rememberSingleCall(block: (I) -> Unit) = remember { SingleCall(block) }

class SingleCall<I>(private val block: (I) -> Unit) {
    private val atomicBoolean = AtomicBoolean(false)

    fun tryExecute(item: I) {
        if (atomicBoolean.compareAndSet(false, true)) {
            block(item)
        }
    }

    fun reset() {
        atomicBoolean.set(false)
    }
}
