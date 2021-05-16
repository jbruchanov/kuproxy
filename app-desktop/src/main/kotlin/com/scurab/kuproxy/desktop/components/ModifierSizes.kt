package com.scurab.kuproxy.desktop.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.scurab.kuproxy.desktop.AppTheme

fun Modifier.defaultMinButtonSize(
    minWidth: Dp = AppTheme.Sizes.buttonMinWidth,
    minHeight: Dp = AppTheme.Sizes.buttonMinHeight
) = then(defaultMinSize(minWidth, minHeight))

fun Modifier.defaultTabIconSize(size: Dp = AppTheme.Sizes.tabIconSize) = then(size(size))
