package com.scurab.kuproxy.desktop

import com.scurab.kuproxy.desktop.screen.main.MainScreen
import com.scurab.kuproxy.desktop.screen.main.MainScreenViewModel

fun main() {
    val viewModel = MainScreenViewModel().also { it.start() }
    MainScreen(viewModel)
}
