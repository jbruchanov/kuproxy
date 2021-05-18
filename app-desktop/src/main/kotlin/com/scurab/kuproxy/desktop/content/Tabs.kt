package com.scurab.kuproxy.desktop.content

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.components.defaultMinButtonSize
import com.scurab.kuproxy.desktop.components.defaultTabIconSize
import com.scurab.kuproxy.desktop.components.drawTabButtonBorder
import com.scurab.kuproxy.desktop.ext.firstIfTrueElseSecond
import com.scurab.kuproxy.desktop.screen.main.TabItem

@Composable
fun TabsRow(
    tabs: List<TabItem>,
    selectedTab: TabItem,
    checkedTab: TabItem?,
    onClick: (TabItem) -> Unit,
    onCheck: (TabItem) -> Unit,
    onClose: (TabItem) -> Unit,
    onNewTab: () -> Unit,
) {
    Box {
        val hScrollState = rememberScrollState()
        Row(modifier = Modifier.horizontalScroll(hScrollState)) {
            tabs.forEach { tab ->
                TabButton(
                    tab,
                    selected = tab == selectedTab,
                    checked = tab == checkedTab,
                    onClick = onClick,
                    onCheck = onCheck,
                    onClose = onClose
                )
            }
            //new Tab
            Box(
                modifier = Modifier
                    .background(AppTheme.Colors.backgroundTabs.firstIfTrueElseSecond(false))
                    .defaultMinButtonSize()
                    .drawTabButtonBorder()
                    .clickable { onNewTab() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
            }
        }
        HorizontalScrollbar(
            rememberScrollbarAdapter(hScrollState),
            modifier = Modifier
                .alpha(0.25f)
                .fillMaxWidth()
                .align(Alignment.BottomStart),
        )
    }
}

@Composable
fun TabButton(
    tab: TabItem,
    selected: Boolean,
    checked: Boolean,
    onClick: (TabItem) -> Unit,
    onCheck: (TabItem) -> Unit = {},
    onClose: (TabItem) -> Unit = {},
) {
    TabButton(
        name = tab.name,
        checkable = tab.checkable,
        closable = tab.closable,
        selected = selected,
        checked = checked,
        onClick = { onClick(tab) },
        onCheck = { onCheck(tab) },
        onClose = { onClose(tab) }
    )
}

@Composable
fun SimpleTabButton(
    name: String,
    selected: Boolean,
    onClick: (String) -> Unit
) {
    TabButton(
        name = name,
        checkable = false,
        closable = false,
        selected = selected,
        checked = false,
        onClick = onClick
    )
}

@Composable
fun TabButton(
    name: String,
    checkable: Boolean,
    closable: Boolean,
    selected: Boolean,
    checked: Boolean,
    onClick: (String) -> Unit,
    onCheck: (String) -> Unit = {},
    onClose: (String) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .background(AppTheme.Colors.backgroundTabs.firstIfTrueElseSecond(selected))
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Max)
            .defaultMinButtonSize()
            .clickable { onClick(name) },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .drawTabButtonBorder()
                .fillMaxHeight()
                .padding(horizontal = AppTheme.Spacing.step)
        ) {
            if (checkable) {
                Checkbox(
                    checked, onCheckedChange = { onCheck(name) },
                    modifier = Modifier.padding(horizontal = AppTheme.Spacing.step_0_5)
                )
            }
            Spacer(modifier = Modifier.width(AppTheme.Spacing.step))
            Text(
                name,
                color = AppTheme.Colors.secondaryOnBackground.firstIfTrueElseSecond(selected),
                modifier = Modifier.defaultMinSize(minWidth = 100.dp)
            )
            if (closable) {
                Spacer(modifier = Modifier.width(AppTheme.Spacing.step))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = { onClose(name) })
                        .padding(AppTheme.Spacing.step_0_5)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.defaultTabIconSize())
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.Sizes.divider)
                .background(color = AppTheme.Colors.primaryTransparent.firstIfTrueElseSecond(selected))
                .align(Alignment.TopStart)

        )
    }
}
