package com.scurab.kuproxy.desktop

import androidx.compose.desktop.Window
import androidx.compose.material.Text

fun main() {
    DesktopApp()
}

fun DesktopApp() = Window(
    centered = false
) {
    Text("Test")
}
