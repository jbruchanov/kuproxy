package com.scurab.kuproxy.desktop

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(colors = AppTheme.Colors.colors) {
        CompositionLocalProvider(
            LocalScrollbarStyle provides AppTheme.scrollBarStyle,
            LocalTextStyle provides AppTheme.TextStyles.default,
            LocalTexts provides EnTexts,
        ) {
            content()
        }
    }
}

val LocalDateTimeFormats = staticCompositionLocalOf { DateTimeFormats() }
val LocalTexts = compositionLocalOf(structuralEqualityPolicy()) { EnTexts }

@Immutable
data class DateTimeFormats(
    val longDateTime: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
)

object AppTheme {
    object Spacing {
        val harline = 1.dp
        val step_0_25 = 2.dp
        val step_0_5 = 4.dp
        val step = 8.dp
        val step_2 = 16.dp
        val step_2_5 = 24.dp
        val step_4 = 32.dp
        val step_6 = 48.dp
        val step_8 = 64.dp
        val step_12 = 96.dp
    }

    object Shapes {
        val textField = RoundedCornerShape(Spacing.step_0_5)
    }

    object Sizes {
        val border = Spacing.harline
        val divider = Spacing.step_0_25
        val resizingBar = divider
        val scrollBar = Spacing.step
        val buttonMinHeight = 40.dp
        val buttonMinWidth = 64.dp
        val tabIconSize = 16.dp
    }

    object Scales {
        val pressed = 0.95f
        val default = 1f

        val pressedDefault = Pair(pressed, default)
    }

    object TextSizes {
        val small = 12.sp
        val default = 14.sp
    }

    object Colors {
        val transparent = Color.Transparent
        val textOnBackground = Color.White
        val textOnBackgroundDisabled = Color(0xFF808080)
        val backgroundContent = Color(0xFF2B2B2B)
        val backgroundControl = Color(0xFF37393b)
        val backgroundRows = Pair(backgroundControl, Color(0xFF313335))
        val backgroundTabs = Pair(backgroundControl, backgroundContent)
        val tabBorder = backgroundControl

        val darkMaterial = darkColors(
            primary = Color(0xFF546E7A),
            primaryVariant = Color(0xFF90A4AE),
            secondary = Color(0xFFCDDC39),
            secondaryVariant = Color(0xFF9E9D24),
            surface = backgroundContent,
            background = backgroundContent,
            onBackground = textOnBackground,
            onPrimary = textOnBackground
        )

        val colors = darkMaterial

        val primaryTransparent = Pair(colors.primary, transparent)
        val primaryPrimaryVariant = Pair(colors.primary, colors.primaryVariant)
        val primaryOnBackground = Pair(colors.primary, colors.onBackground)
        val secondaryOnBackground = Pair(colors.secondary, colors.onBackground)
    }

    object TextStyles {
        val default = TextStyle(
            color = Colors.colors.onBackground
        )

        val disabled = TextStyle(
            color = Colors.textOnBackgroundDisabled
        )

        val monoSpaceTextStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            color = Colors.colors.onBackground
        )
        val boldNormal = Pair(FontWeight.Bold, FontWeight.Normal)
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
