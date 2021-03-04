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
package com.example.androiddevchallenge.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.androiddevchallenge.util.ThemedPreview

@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier, navigateToAdd: () -> Unit) {
    if (!viewModel.hasTimer()) {
        navigateToAdd()
        return
    }
    val time: Long by viewModel.tick.observeAsState(0)
    MainScreenBody("")
}

@Composable
fun MainScreenBody(
    formattedTime: String = ""
) {
}

@Preview("Main screen body")
@Composable
fun PreviewHomeScreenBody() {
    ThemedPreview {
        MainScreenBody()
    }
}

@Preview("Main screen body dark")
@Composable
fun PreviewHomeScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        MainScreenBody()
    }
}
