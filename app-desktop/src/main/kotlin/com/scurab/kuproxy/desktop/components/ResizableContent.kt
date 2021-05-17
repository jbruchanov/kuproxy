package com.scurab.kuproxy.desktop.components

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scurab.kuproxy.desktop.AppTheme
import java.awt.Cursor

@Composable
fun VerticalResizingContent(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    ResizingContent(left, right, Orientation.Vertical, modifier, isEnabled)
}

@Composable
fun HorizontalResizingContent(
    top: @Composable () -> Unit,
    bottom: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    ResizingContent(top, bottom, Orientation.Horizontal, modifier, isEnabled)
}

@Composable
fun ResizingContent(
    start: @Composable () -> Unit,
    end: @Composable () -> Unit,
    orientation: Orientation = Orientation.Vertical,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    Box(
        modifier = Modifier.fillMaxSize().then(modifier)
    ) {

        val window = LocalAppWindow.current
        var size by remember {
            mutableStateOf(
                (if (orientation == Orientation.Vertical) window.width else window.height) / 2f
            )
        }

        when (orientation) {
            Orientation.Vertical -> {
                Row {
                    Box(modifier = Modifier.fillMaxHeight().width(size.dp)) {
                        start()
                    }
                    ResizingDivider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(AppTheme.Sizes.resizingBar),
                        onDrag = { size += it },
                        orientation = Orientation.Horizontal,
                        cursor = Cursor(Cursor.E_RESIZE_CURSOR),
                        isEnabled = isEnabled
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        end()
                    }
                }
            }
            Orientation.Horizontal -> {
                Column {
                    Box(modifier = Modifier.fillMaxWidth().height(size.dp)) {
                        start()
                    }
                    ResizingDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.Sizes.resizingBar),
                        onDrag = { size += it },
                        orientation = Orientation.Vertical,
                        cursor = Cursor(Cursor.N_RESIZE_CURSOR),
                        isEnabled = isEnabled
                    )
                    Box(modifier = Modifier.fillMaxSize()) {
                        end()
                    }
                }
            }
        }
    }
}

@Composable
private fun ResizingDivider(
    modifier: Modifier,
    onDrag: (Float) -> Unit,
    orientation: Orientation,
    cursor: Cursor,
    isEnabled: Boolean = true,
) {
    Box(
        modifier = Modifier
            .run {
                if (isEnabled) {
                    draggable(
                        state = rememberDraggableState(onDrag),
                        orientation = orientation,
                        startDragImmediately = true
                    )
                } else this
            }
            .mouseCursor(cursor)
            .background(AppTheme.Colors.backgroundControl)
            .run {
                if (orientation == Orientation.Vertical) {
                    padding(vertical = AppTheme.Spacing.step_0_5)
                } else {
                    padding(horizontal = AppTheme.Spacing.step_0_5)
                }
            }
            .background(AppTheme.Colors.colors.primary)
            .then(modifier)
    ) {
    }
}
