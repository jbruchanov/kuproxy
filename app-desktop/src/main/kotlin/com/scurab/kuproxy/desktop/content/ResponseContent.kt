package com.scurab.kuproxy.desktop.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.comm.Headers.headerCharset
import com.scurab.kuproxy.comm.Headers.isImageContent
import com.scurab.kuproxy.comm.Headers.isTextContent
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.storage.RequestResponse
import org.jetbrains.skija.Image as SkijaImage

@Composable
fun ResponseContent(item: RequestResponse) {
    SelectionContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val (req, resp) = item
            Text("URL: ${req.url}")
            Text("Status: ${resp.status}")
            resp.headers.forEach { (key, value) ->
                Text("$key: $value")
            }
            Spacer(modifier = Modifier.height(AppTheme.Spacing.step))
            val content = resp.headers.entries
                .firstOrNull { (k, _) -> k.toLowerCase() == Headers.ContentType }
                ?.value
            resp.notEmptyBody?.let { body ->
                when {
                    content?.isTextContent() == true -> {
                        Text(String(body, content.headerCharset()))
                    }
                    content?.isImageContent() == true -> Image(SkijaImage.makeFromEncoded(resp.body).asImageBitmap(), contentDescription = null)
                    else -> Text("BodyLen:${body.size}")
                }
            }
        }
    }
}
