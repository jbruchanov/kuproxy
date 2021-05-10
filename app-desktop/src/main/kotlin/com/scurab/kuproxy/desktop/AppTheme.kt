package com.scurab.kuproxy.desktop

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = AppTheme.Colors.colors) {
        CompositionLocalProvider(
            LocalScrollbarStyle provides AppTheme.scrollBarStyle
        ) {
            content()
        }
    }
}

object AppTheme {
    object Spacing {
        val harline = 1.dp
        val step_0_25 = 2.dp
        val step_0_5 = 4.dp
        val step = 8.dp
        val step_2 = 16.dp
        val step_4 = 32.dp
        val step_8 = 64.dp
    }

    object Sizes {
        val resizingBar = Spacing.step_0_25
        val scrollBar = Spacing.step
    }

    object Colors {
        val backgroundContent = Color(0xFF2B2B2B)
        val backgroundControl = Color(0xFF37393b)
        val backgroundEven = Color(0xFF37393b)
        val backgroundOdd = Color(0xFF313335)

        val darkMaterial = darkColors(
            primary = Color(0xFFFF7800),
            primaryVariant = Color(0xFFFF9046),
            surface = backgroundContent,
            background = backgroundContent
        )

        val colors = darkMaterial
    }

    val scrollBarStyle = ScrollbarStyle(
        minimalHeight = Spacing.step_4,
        thickness = Sizes.scrollBar,
        shape = RectangleShape,
        hoverDurationMillis = 0,
        unhoverColor = Colors.colors.primary,
        hoverColor = Colors.colors.primaryVariant
    )
}
