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
package com.example.androiddevchallenge.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.ui.main.MainScreen
import com.example.androiddevchallenge.ui.theme.JettimerTheme
import com.example.androiddevchallenge.util.Screen
import com.example.androiddevchallenge.util.hiltNavGraphViewModel
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun JettimerApp() {
    ProvideWindowInsets {
        JettimerTheme {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screen.Main.route) {
                composable(Screen.Main.route) {
                    MainScreen(viewModel = it.hiltNavGraphViewModel())
                }
                // Add new screen
            }
        }
    }
}
