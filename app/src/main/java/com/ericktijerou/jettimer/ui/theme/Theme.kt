/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ericktijerou.jettimer.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.ericktijerou.jettimer.util.LocalSysUiController

private val DarkColorPalette = darkColors(
    primary = BlackLight,
    primaryVariant = BlackLight,
    secondary = Teal200,
    secondaryVariant = TimerActiveDark,
    background = BlackLight,
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val DarkTimerColorPalette = JettimerColors(
    textPrimaryColor = Color.White,
    textSecondaryColor = TextSecondaryDark,
    searchBoxColor = GraySearchBoxDark,
    textVariantColor = Color.Black,
    isDark = true
)

@Composable
fun JettimerTheme(content: @Composable () -> Unit) {
    val sysUiController = LocalSysUiController.current
    SideEffect {
        sysUiController.setSystemBarsColor(
            color = DarkColorPalette.primary
        )
    }
    ProvideJettimerColors(DarkTimerColorPalette) {
        MaterialTheme(
            colors = DarkColorPalette,
            typography = typography,
            shapes = Shapes,
            content = content
        )
    }
}

object JettimerTheme {
    val colors: JettimerColors
        @Composable
        get() = LocalJettimerColors.current
}

@Composable
fun ProvideJettimerColors(
    colors: JettimerColors,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalJettimerColors provides colorPalette, content = content)
}

@Stable
class JettimerColors(
    textPrimaryColor: Color,
    textSecondaryColor: Color,
    searchBoxColor: Color,
    textVariantColor: Color,
    isDark: Boolean
) {
    var textPrimaryColor by mutableStateOf(textPrimaryColor)
        private set
    var textSecondaryColor by mutableStateOf(textSecondaryColor)
        private set
    var searchBoxColor by mutableStateOf(searchBoxColor)
        private set
    var textVariantColor by mutableStateOf(textVariantColor)
        private set
    var isDark by mutableStateOf(isDark)
        private set

    fun update(other: JettimerColors) {
        textPrimaryColor = other.textPrimaryColor
        textSecondaryColor = other.textSecondaryColor
        searchBoxColor = other.searchBoxColor
        textVariantColor = other.textVariantColor
        isDark = other.isDark
    }
}

private val LocalJettimerColors = staticCompositionLocalOf<JettimerColors> {
    error("No JetsnackColorPalette provided")
}
