package com.example.androiddevchallenge.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.androiddevchallenge.ui.theme.JettimerTheme
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun JettimerApp() {
    ProvideWindowInsets {
        JettimerTheme {
            Surface(color = MaterialTheme.colors.background) {
                Text(text = "Ready... Set... GO!")
            }
        }
    }
}
