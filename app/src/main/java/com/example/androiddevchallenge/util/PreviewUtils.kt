package com.example.androiddevchallenge.util

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.example.androiddevchallenge.ui.theme.JettimerTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    JettimerTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}