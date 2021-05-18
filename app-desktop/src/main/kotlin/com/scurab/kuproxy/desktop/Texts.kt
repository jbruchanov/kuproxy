package com.scurab.kuproxy.desktop

import androidx.compose.ui.text.AnnotatedString
import com.scurab.kuproxy.desktop.ext.ans

interface Texts {
    val domains: AnnotatedString
    val port: AnnotatedString
    val ok: AnnotatedString
    val cancel: AnnotatedString
    val proxy: AnnotatedString
    val passthrough: AnnotatedString
    val replay: AnnotatedString
    val record: AnnotatedString
    val request: AnnotatedString
    val response: AnnotatedString
}

object EnTexts : Texts {
    override val domains: AnnotatedString = "Domains".ans
    override val port: AnnotatedString = "Port".ans
    override val ok: AnnotatedString = "OK".ans
    override val cancel: AnnotatedString = "Cancel".ans
    override val proxy: AnnotatedString = "Proxy".ans
    override val passthrough: AnnotatedString = "Passthrough".ans
    override val replay: AnnotatedString = "Replay".ans
    override val record: AnnotatedString = "Record".ans
    override val request: AnnotatedString = "Request".ans
    override val response: AnnotatedString = "Response".ans
}
