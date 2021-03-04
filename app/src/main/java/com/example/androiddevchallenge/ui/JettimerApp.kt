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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import androidx.navigation.compose.rememberNavController
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.add.AddScreen
import com.example.androiddevchallenge.ui.main.MainScreen
import com.example.androiddevchallenge.ui.theme.JettimerTheme
import com.example.androiddevchallenge.util.Screen
import com.example.androiddevchallenge.util.hiltNavGraphViewModel
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun JettimerApp() {
    ProvideWindowInsets {
        JettimerTheme {
            Scaffold(
                topBar = { MainAppBar() }
            ) { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                val navController = rememberNavController()
                NavHost(navController, startDestination = Screen.Main.route) {
                    composable(Screen.Main.route) {
                        MainScreen(viewModel = it.hiltNavGraphViewModel(), modifier = modifier) {
                            navController.navigate(route = Screen.AddTimer.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }
                        }
                    }
                    composable(Screen.AddTimer.route) {
                        AddScreen(viewModel = it.hiltNavGraphViewModel(), modifier = modifier) {
                            navController.navigate(route = Screen.Main.route) {
                                popUpTo(Screen.AddTimer.route) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primary
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.label_timer),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.label_actions)
            )
        },
        backgroundColor = backgroundColor,
        modifier = modifier,
        elevation = 0.dp
    )
}
