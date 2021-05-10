package com.scurab.kuproxy.desktop

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.scurab.kuproxy.desktop.components.AppVerticalScrollbar
import com.scurab.kuproxy.desktop.components.HorizontalResizingContent

fun main() {
    DesktopApp()
}

private val items = (0..1000).map { "http://sample.something.com/$it" }

@OptIn(ExperimentalFoundationApi::class)
fun DesktopApp() = Window(
    centered = false
) {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                },
                bottomBar = {
                }
            ) {
                HorizontalResizingContent(
                    top = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val scrollState = rememberLazyListState(0, 0)
                            LazyColumn(
                                state = scrollState,
                                modifier = Modifier.weight(1f)
                            ) {
                                items(items.size) {
                                    Text(items[it])
                                }
                            }
                            AppVerticalScrollbar(adapter = ScrollbarAdapter(scrollState, items.size, 20f))
                        }
                    },
                    bottom = {
                        Box {
                            Text("Bottom")
                        }
                    }
                )
            }
        }
    }
}
