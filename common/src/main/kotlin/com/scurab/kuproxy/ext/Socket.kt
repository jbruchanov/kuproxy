package com.scurab.kuproxy.ext

import java.net.Socket

fun Socket.closeQuietly() = runCatching { if (!isClosed) close() }.isSuccess
