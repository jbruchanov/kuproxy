package com.scurab.kuproxy.desktop.screen.main

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.components.AppVerticalScrollbar
import com.scurab.kuproxy.desktop.components.HorizontalResizingContent
import com.scurab.kuproxy.desktop.components.arrowKeys
import com.scurab.kuproxy.desktop.content.RequestRow
import com.scurab.kuproxy.desktop.content.ResponseContent
import com.scurab.kuproxy.desktop.ext.firstOddElseSecond
import com.scurab.kuproxy.storage.RequestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainWindowState {
    val items = mutableStateListOf<RequestResponse>()
    var isConfigVisible by mutableStateOf(false)
    var selectedIndex by mutableStateOf<Int?>(null)
}

@OptIn(ExperimentalFoundationApi::class)
fun MainScreen(viewModel: MainScreenViewModel = MainScreenViewModel()) = Window(
    centered = false
) {
    val dataScrollState = LazyListState(viewModel.state.items.size - 1, 0)

    AppTheme {
        Surface(
            modifier = Modifier
                .arrowKeys {
                    viewModel.onKeyArrowClicked(it)?.also { GlobalScope.launch(Dispatchers.Main) { dataScrollState.scrollToItem(it) } }
                }
                .fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    Row {
                        Button(onClick = { viewModel.onDeleteClicked() }, contentPadding = PaddingValues(0.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
                        }
                        Button(onClick = { viewModel.onSettingsClicked() }, contentPadding = PaddingValues(0.dp)) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
                        }
                    }
                },
                bottomBar = {
                    Box {
                    }
                }
            ) {
                val state = viewModel.state
                CompositionLocalProvider(
                    LocalTextStyle provides AppTheme.TextStyles.monoSpaceTextStyle,
                ) {
                    HorizontalResizingContent(
                        top = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LazyColumn(
                                    state = dataScrollState,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(state.items.size) { index ->
                                        val item = state.items[index]
                                        RequestRow(
                                            index,
                                            item,
                                            onClick = { viewModel.onItemSelected(it) },
                                            modifier = Modifier
                                                .background(
                                                    AppTheme.Colors.colors.primaryVariant.takeIf { index == state.selectedIndex }
                                                        ?: AppTheme.Colors.backgroundPair.firstOddElseSecond(index)
                                                )
                                        )
                                    }
                                }
                                AppVerticalScrollbar(adapter = ScrollbarAdapter(dataScrollState, state.items.size, 20f))
                            }
                        },
                        bottom = {
                            Column(modifier = Modifier.padding(AppTheme.Spacing.step).fillMaxSize()) {
                                state.selectedIndex?.let {
                                    ResponseContent(state.items[it])
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (viewModel.state.isConfigVisible) {
        ConfigWindow(viewModel)
    }
}
