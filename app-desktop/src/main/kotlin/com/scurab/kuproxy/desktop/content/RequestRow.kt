package com.scurab.kuproxy.desktop.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.LocalDateTimeFormats
import com.scurab.kuproxy.storage.RequestResponse

@Composable
fun RequestRow(index: Int, item: RequestResponse, onClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val formats = LocalDateTimeFormats.current
    Row(
        modifier = Modifier
            .padding(vertical = AppTheme.Spacing.harline)
            .defaultMinSize(minHeight = AppTheme.Spacing.step_2_5)
            .clickable { onClick(index) }
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.Spacing.step),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (req, resp) = item
        Spacer(modifier.width(AppTheme.Spacing.step_0_25))
        Text(resp.status.takeIf { it > 0 }?.toString() ?: "---")
        Text(req.method, modifier.defaultMinSize(AppTheme.Spacing.step_4), textAlign = TextAlign.Center)
        Text(req.url.host, fontWeight = FontWeight.Bold)
        Text(
            req.url.path,
            fontSize = AppTheme.TextSizes.small,
            fontWeight = FontWeight.Thin,
            modifier = Modifier.defaultMinSize(AppTheme.Spacing.step_12).weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(resp.body.size.toString() + "b", modifier.defaultMinSize(AppTheme.Spacing.step_6), textAlign = TextAlign.Right)
        Text(formats.longDateTime.format(req.recorded))
        Spacer(modifier.width(AppTheme.Spacing.step_0_25))
    }
}
