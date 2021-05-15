package com.scurab.kuproxy.desktop.screen.main

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.scurab.kuproxy.desktop.AppTheme
import com.scurab.kuproxy.desktop.LocalTexts
import com.scurab.kuproxy.desktop.components.InputTextField
import com.scurab.kuproxy.desktop.screen.model.AppConfig
import com.scurab.kuproxy.desktop.util.rememberSingleCall
import com.scurab.kuproxy.ext.toPortNumberOrNull
import java.awt.Dimension

@Composable
fun ConfigWindow(viewModel: MainScreenViewModel) = Window(
    size = IntSize(640, 320)
) {
    val state = viewModel.state
    val window = LocalAppWindow.current
    val texts = LocalTexts.current
    val config by remember { mutableStateOf(AppConfig.fromProxyConfig(viewModel.config)) }
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(AppTheme.Spacing.step)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val setMinSize = rememberSingleCall<LayoutCoordinates> {
                    window.window.minimumSize = Dimension(640, (it.size.height + 100).coerceAtMost(768))
                }
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { setMinSize.tryExecute(it) }
                ) {
                    Text(texts.port)
                    InputTextField(
                        value = config.port,
                        maxLines = 1,
                        onValueChange = { v ->
                            if (v.isEmpty() || v.toPortNumberOrNull() != null) {
                                config.port = v
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(texts.domains)
                    InputTextField(
                        value = config.domains,
                        maxLines = 15,
                        textStyle = AppTheme.TextStyles.monoSpaceTextStyle,
                        onValueChange = { v -> config.domains = v },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(AppTheme.Spacing.step))
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { window.close() }) {
                        Text(texts.cancel)
                    }
                    Spacer(modifier = Modifier.width(AppTheme.Spacing.step))
                    Button(onClick = { viewModel.onConfigSaved(config.toConfig()); window.close() }) {
                        Text(texts.ok)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            state.isConfigVisible = false
        }
    }
}
