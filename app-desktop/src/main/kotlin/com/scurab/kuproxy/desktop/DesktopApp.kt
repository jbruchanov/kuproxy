package com.scurab.kuproxy.desktop

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier

fun main() {
    DesktopApp()
}

fun DesktopApp() = Window(
    centered = false
) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box {
                Text("Test")
            }
        }
    }
}
