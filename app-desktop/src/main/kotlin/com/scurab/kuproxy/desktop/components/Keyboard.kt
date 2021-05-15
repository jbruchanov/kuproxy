package com.scurab.kuproxy.desktop.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type

@Composable
fun Modifier.arrowKeys(
    onChange: (Int) -> Unit
): Modifier {
    return this.then(
        onKeyEvent {
            if (it.type == KeyEventType.KeyUp) {
                when (it.key) {
                    Key.DirectionUp -> onChange(-1)
                    Key.DirectionDown -> onChange(1)
                }
                true
            } else {
                false
            }
        }
    )
}
