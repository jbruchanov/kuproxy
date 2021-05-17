package com.scurab.kuproxy.desktop.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.components.defaultMinButtonSize

@Composable
fun AppMenu(viewModel: MainScreenViewModel) {
    Row(
        modifier = Modifier.padding(AppTheme.Spacing.step).height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.Spacing.step)
    ) {
        AppMenuButton(Icons.Default.Delete, onClick = { viewModel.onDeleteClicked() })
        AppMenuButton(Icons.Default.Save, onClick = { })
        AppMenuModeDropDown(viewModel)
        Box(modifier = Modifier.weight(1f))
        AppMenuButton(Icons.Default.Security, onClick = { })
        AppMenuButton(Icons.Default.Settings, onClick = { viewModel.onSettingsClicked() })
    }
}

@Composable
private fun AppMenuButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(onClick = onClick, contentPadding = PaddingValues(0.dp), modifier = Modifier.defaultMinButtonSize()) {
        Icon(icon, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
    }
}

@Composable
private fun AppMenuModeDropDown(viewModel: MainScreenViewModel) {
    val state = viewModel.state
    Box {
        val dropDownItems = remember { Mode.values() }
        Button(onClick = { state.modeDropDownMenuExpanded = true }, modifier = Modifier.defaultMinButtonSize()) {
            Icon(state.mode.icon, contentDescription = null)
            Spacer(modifier = Modifier.width(AppTheme.Spacing.step_2))
            Text(state.mode.textValue(), maxLines = 1)
            Spacer(modifier = Modifier.width(AppTheme.Spacing.step))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
        }
        DropdownMenu(
            state.modeDropDownMenuExpanded,
            onDismissRequest = { state.modeDropDownMenuExpanded = false },
        ) {
            dropDownItems.forEach { mode ->
                DropdownMenuItem(
                    onClick = { viewModel.onModeChanged(mode) },
                    modifier = Modifier.defaultMinSize(minWidth = 180.dp)
                ) {
                    Icon(mode.icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(AppTheme.Spacing.step_2))
                    Text(mode.textValue())
                }
            }
        }
    }
}
