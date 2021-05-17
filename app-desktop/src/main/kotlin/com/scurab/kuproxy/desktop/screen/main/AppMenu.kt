package com.scurab.kuproxy.desktop.screen.main

import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.components.defaultMinButtonSize
import com.scurab.kuproxy.desktop.components.scalingOnPressed
import com.scurab.kuproxy.desktop.ext.firstIfTrueElseSecond

@Composable
fun AppMenu(viewModel: MainScreenViewModel) {
    Row(
        modifier = Modifier.padding(AppTheme.Spacing.step).height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.Spacing.step)
    ) {
        AppMenuToggleButton(viewModel.state.keepScrolledBottom, onClick = { viewModel.onKeepScrolledBottomClicked() }, icon = Icons.Default.VerticalAlignBottom)
        AppMenuButton(onClick = { viewModel.onDeleteClicked() }, Icons.Default.Delete)
        AppMenuButton(onClick = { }, Icons.Default.Save)
        AppMenuModeDropDown(viewModel)
        Box(modifier = Modifier.weight(1f))
        AppMenuButton(onClick = { }, Icons.Default.Security)
        AppMenuButton(onClick = { viewModel.onSettingsClicked() }, Icons.Default.Settings)
    }
}

@Composable
private fun AppMenuButton(
    onClick: () -> Unit,
    icon: ImageVector,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = colors,
        modifier = Modifier.defaultMinButtonSize().scalingOnPressed(interactionSource).then(modifier),
        interactionSource = interactionSource
    ) {
        Icon(icon, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
    }
}

@Composable
private fun AppMenuToggleButton(
    isChecked: Boolean,
    onClick: (Boolean) -> Unit,
    icon: ImageVector,
    colors: ButtonColors = ButtonDefaults.buttonColors(AppTheme.Colors.primaryPrimaryVariant.firstIfTrueElseSecond(!isChecked)),
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Button(
        onClick = { onClick(!isChecked) },
        contentPadding = PaddingValues(0.dp),
        colors = colors,
        modifier = Modifier.defaultMinButtonSize().scalingOnPressed(interactionSource).then(modifier),
        interactionSource = interactionSource
    ) {
        Icon(icon, contentDescription = null, tint = AppTheme.Colors.colors.onPrimary)
    }
}

@Composable
private fun AppMenuModeDropDown(
    viewModel: MainScreenViewModel,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val state = viewModel.state
    Box {
        val dropDownItems = remember { Mode.values() }
        Button(
            onClick = { state.modeDropDownMenuExpanded = true },
            interactionSource = interactionSource,
            modifier = Modifier.defaultMinButtonSize().scalingOnPressed(interactionSource)
        ) {
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
