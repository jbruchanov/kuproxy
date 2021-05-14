package com.scurab.kuproxy.desktop

import androidx.compose.ui.text.AnnotatedString
import com.scurab.kuproxy.desktop.ext.ans

interface Texts {
    val domains: AnnotatedString
    val port: AnnotatedString
    val ok: AnnotatedString
    val cancel: AnnotatedString
}

object EnTexts : Texts {
    override val domains: AnnotatedString = "Domains".ans
    override val port: AnnotatedString = "Port".ans
    override val ok: AnnotatedString = "OK".ans
    override val cancel: AnnotatedString = "Cancel".ans
}
