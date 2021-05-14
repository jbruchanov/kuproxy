package com.scurab.kuproxy.desktop.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.LocalDateTimeFormats
import com.scurab.kuproxy.storage.RequestResponse

@Composable
fun RequestRow(item: RequestResponse, onClick: (RequestResponse) -> Unit, modifier: Modifier = Modifier) {
    val formats = LocalDateTimeFormats.current
    Row(
        modifier = Modifier
            .padding(AppTheme.Spacing.step_0_25)
            .clickable { onClick(item) }
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.Spacing.step)
    ) {
        val (req, resp) = item
        Text(resp.status.toString())
        Text(req.method, modifier.defaultMinSize(AppTheme.Spacing.step_6), textAlign = TextAlign.Center)
        Text(req.url.host)
        Text(req.url.path, modifier = Modifier.defaultMinSize(AppTheme.Spacing.step_12).weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(resp.body.size.toString() + "b", modifier.defaultMinSize(AppTheme.Spacing.step_6), textAlign = TextAlign.Right)
        Text(formats.longDateTime.format(req.recorded))
    }
}
