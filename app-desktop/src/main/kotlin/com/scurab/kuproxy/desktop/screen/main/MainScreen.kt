package com.scurab.kuproxy.desktop.screen.main

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.SendAndArchive
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.EnTexts
import com.scurab.kuproxy.desktop.LocalTexts
import com.scurab.kuproxy.desktop.Texts
import com.scurab.kuproxy.desktop.components.AppVerticalScrollbar
import com.scurab.kuproxy.desktop.components.HorizontalResizingContent
import com.scurab.kuproxy.desktop.components.registerAsDropFileTarget
import com.scurab.kuproxy.desktop.content.BodyContent
import com.scurab.kuproxy.desktop.content.ContentType
import com.scurab.kuproxy.desktop.content.RequestRow
import com.scurab.kuproxy.desktop.content.SimpleTabButton
import com.scurab.kuproxy.desktop.content.TabsRow
import com.scurab.kuproxy.desktop.ext.firstIfTrueElseSecond
import com.scurab.kuproxy.desktop.ext.firstOddElseSecond
import com.scurab.kuproxy.model.TrackingEvent
import com.scurab.kuproxy.storage.RequestResponse

enum class Mode(
    val icon: ImageVector,
    private val text: @Composable Texts.() -> AnnotatedString
) {
    Passthrough(Icons.Default.SyncAlt, { passthrough }),
    Replay(Icons.Default.FindReplace, { replay }),
    Record(Icons.Default.SendAndArchive, { record });

    @Composable
    fun textValue(): AnnotatedString {
        return text(LocalTexts.current)
    }
}

class MainWindowState {
    var keepScrolledBottom by mutableStateOf(true)

    val tabs = mutableStateListOf<TabItem>().also {
        it.add(TabItem(EnTexts.proxy.toString(), closable = false))
    }
    var isConfigVisible by mutableStateOf(false)
    var selectedTab by mutableStateOf(tabs.first())
    var checkedTab by mutableStateOf<TabItem?>(null)

    val currentTabState get() = selectedTab.state
    val proxyTabState get() = tabs[0].state

    var modeDropDownMenuExpanded by mutableStateOf(false)
    var mode by mutableStateOf(Mode.Passthrough)
}

data class TabItem(
    val name: String,
    val closable: Boolean = false,
    val checkable: Boolean = false,
    val state: TabState = TabState()
)

class TabState() {
    val items = mutableStateListOf<TrackingEvent>()
    var selectedRowIndex by mutableStateOf(-1)
    var selectedContentType by mutableStateOf(ContentType.Response)

    constructor(collection: Collection<RequestResponse>) : this() {
        items.addAll(collection.map { TrackingEvent(it) })
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun MainScreen(viewModel: MainScreenViewModel = MainScreenViewModel()) = Window(
    centered = false
) {
    registerAsDropFileTarget { viewModel.onLoadFiles(it) }

    val state = viewModel.state
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                AppMenu(viewModel)
                CompositionLocalProvider(LocalTextStyle provides AppTheme.TextStyles.monoSpaceTextStyle) {
                    HorizontalResizingContent(
                        top = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                CompositionLocalProvider(LocalTextStyle provides AppTheme.TextStyles.default) {
                                    TabsRow(
                                        state.tabs,
                                        state.selectedTab,
                                        state.checkedTab,
                                        onClick = { viewModel.onTabClicked(it) },
                                        onCheck = { viewModel.onTabChecked(it) },
                                        onClose = { viewModel.onTabClosed(it) },
                                        onNewTab = { viewModel.addNewTab() }
                                    )
                                }
                                TabDataContent(state.keepScrolledBottom, state.currentTabState, onRowClick = { viewModel.onItemSelected(it) })
                            }
                        },
                        bottom = {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                val texts = LocalTexts.current
                                Row {
                                    SimpleTabButton(
                                        texts.request.toString(),
                                        selected = state.currentTabState.selectedContentType == ContentType.Request,
                                        onClick = { viewModel.onContentTypeClicked(ContentType.Request) }
                                    )

                                    SimpleTabButton(
                                        texts.response.toString(),
                                        state.currentTabState.selectedContentType == ContentType.Response,
                                        onClick = { viewModel.onContentTypeClicked(ContentType.Response) }
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(AppTheme.Colors.backgroundTabs.firstIfTrueElseSecond(true))
                                ) {
                                    val tabState = state.currentTabState
                                    tabState.selectedRowIndex
                                        .takeIf { it != -1 }
                                        ?.let {
                                            BodyContent(
                                                tabState.items[it],
                                                state.currentTabState.selectedContentType,
                                                Modifier.padding(AppTheme.Spacing.step)
                                            )
                                        }
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

@ExperimentalFoundationApi
@Composable
private fun TabDataContent(
    keepScrolledBottom: Boolean,
    state: TabState,
    onRowClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.background(AppTheme.Colors.backgroundTabs.firstIfTrueElseSecond(true)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val manualScrollState = remember(keepScrolledBottom) { LazyListState(Integer.MAX_VALUE) }
        val keepBottomState = LazyListState(Integer.MAX_VALUE)
        val scrollState = if (keepScrolledBottom) keepBottomState else manualScrollState
        LazyColumn(
            state = scrollState,
            modifier = Modifier.weight(1f)
        ) {
            items(state.items.size) { index ->
                val item = state.items[index]
                RequestRow(
                    index,
                    item.requestResponse,
                    onClick = onRowClick,
                    modifier = Modifier
                        .background(
                            AppTheme.Colors.colors.primary.takeIf { index == state.selectedRowIndex }
                                ?: AppTheme.Colors.backgroundRows.firstOddElseSecond(index)
                        )
                )
            }
        }
        AppVerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState, state.items.size, 26.dp))
    }
}
