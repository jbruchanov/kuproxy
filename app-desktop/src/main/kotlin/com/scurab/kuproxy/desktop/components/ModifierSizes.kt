package com.scurab.kuproxy.desktop.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.ext.firstIfTrueElseSecond
import kotlinx.coroutines.flow.collect

fun Modifier.defaultMinButtonSize(
    minWidth: Dp = AppTheme.Sizes.buttonMinWidth,
    minHeight: Dp = AppTheme.Sizes.buttonMinHeight
) = then(defaultMinSize(minWidth, minHeight))

fun Modifier.defaultTabIconSize(size: Dp = AppTheme.Sizes.tabIconSize) = then(size(size))

@Composable
fun Modifier.scalingOnPressed(interactionSource: MutableInteractionSource): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(AppTheme.Scales.pressedDefault.firstIfTrueElseSecond(isPressed))
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            isPressed = interaction is PressInteraction.Press
        }
    }
    return this.scale(scale)
}
