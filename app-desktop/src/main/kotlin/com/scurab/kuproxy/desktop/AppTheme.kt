package com.scurab.kuproxy.desktop

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = AppTheme.Colors.colors) {
        content()
    }
}


object AppTheme {

    object Colors {
        val backgroundDefault = Color(0xFF2B2B2B)
        val backgroundEven = Color(0xFF37393b)
        val backgroundOdd = Color(0xFF313335)

        val darkMaterial = darkColors(
            primary = Color(0xFFFF7800),
            primaryVariant = Color(0xFFFF9046),
            surface = backgroundDefault,
            background = backgroundDefault
        )

        val colors = darkMaterial
    }
}