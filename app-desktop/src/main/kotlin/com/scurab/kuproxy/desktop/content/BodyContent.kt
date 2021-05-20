package com.scurab.kuproxy.desktop.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.scurab.kuproxy.comm.Headers
import com.scurab.kuproxy.comm.Headers.headerCharset
import com.scurab.kuproxy.comm.Headers.isImageContent
import com.scurab.kuproxy.comm.Headers.isTextContent
import com.scurab.kuproxy.comm.IRequestResponseCommon
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.ext.firstIfTrueElseSecond
import com.scurab.kuproxy.model.TrackingEvent
import com.scurab.kuproxy.storage.RequestResponse
import org.jetbrains.skija.Image as SkijaImage

enum class ContentType(private val getter: (RequestResponse) -> IRequestResponseCommon) {
    Request({ it.request }), Response({ it.response });

    fun get(requestResponse: RequestResponse): IRequestResponseCommon = getter(requestResponse)
}

@Composable
fun BodyContent(
    item: TrackingEvent,
    contentType: ContentType,
    modifier: Modifier = Modifier
) {
    Row {
        val scrollState = remember(item) { ScrollState(0) }
        Column(
            modifier = Modifier
                .weight(1f)
                .background(AppTheme.Colors.backgroundTabs.firstIfTrueElseSecond(true))
                .verticalScroll(scrollState)
                .then(modifier)
        ) {
            val (req, resp) = item.requestResponse
            val contentItem = contentType.get(item.requestResponse)

            Text("URL: ${req.url}")
            Text("Status: ${resp.status}")
            Divider(
                color = AppTheme.Colors.colors.primary,
                thickness = AppTheme.Sizes.border,
                modifier = Modifier.padding(
                    top = AppTheme.Spacing.step_0_5,
                    bottom = AppTheme.Spacing.step_0_25
                )
            )

            contentItem.headers.forEach { (key, value) ->
                Text("$key: $value")
            }
            Spacer(modifier = Modifier.height(AppTheme.Spacing.step))

            val content = contentItem.headers.entries
                .firstOrNull { (k, _) -> k.toLowerCase() == Headers.ContentType }
                ?.value

            contentItem.notEmptyBody?.let { body ->
                when {
                    content?.isTextContent() == true -> {
                        Text(String(body, content.headerCharset()))
                    }
                    content?.isImageContent() == true -> Image(SkijaImage.makeFromEncoded(resp.body).asImageBitmap(), contentDescription = null)
                    else -> Text("BodyLen:${body.size}")
                }
            }
        }

        VerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState))
    }
}
