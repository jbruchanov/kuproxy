package com.scurab.kuproxy.desktop.components

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerMoveFilter
import java.awt.Cursor

@Composable
private fun setCursor(
    isHover: Boolean,
    isHoverCursor: Cursor,
    defaultCursor: Cursor = Cursor.getDefaultCursor()
) {
    LocalAppWindow.current.window.cursor = isHoverCursor.takeIf { isHover } ?: defaultCursor
}

@Composable
fun Modifier.mouseCursor(cursor: Cursor): Modifier {
    var isHovering by remember { mutableStateOf(false) }
    setCursor(isHovering, cursor)
    return this.pointerMoveFilter(
        onEnter = {
            isHovering = true
            true
        },
        onExit = {
            isHovering = false
            true
        }
    )
}
