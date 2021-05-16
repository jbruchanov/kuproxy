package com.scurab.kuproxy.desktop.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.scurab.kuproxy.desktop.AppTheme

fun Modifier.drawTabButtonBorder() = then(
    drawBehind {
        drawLine(AppTheme.Colors.tabBorder, start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = AppTheme.Sizes.divider.toPx())
        drawLine(AppTheme.Colors.tabBorder, start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = AppTheme.Sizes.divider.toPx())
        drawLine(AppTheme.Colors.tabBorder, start = Offset(size.width, 0f), end = Offset(size.width, size.height), strokeWidth = AppTheme.Sizes.divider.toPx())
    }
)
