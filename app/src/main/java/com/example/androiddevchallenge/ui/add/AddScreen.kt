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
package com.example.androiddevchallenge.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.manager.DataManager
import com.example.androiddevchallenge.ui.theme.JettimerTheme
import com.example.androiddevchallenge.util.EMPTY
import com.example.androiddevchallenge.util.ThemedPreview
import com.example.androiddevchallenge.util.fillWithZeros
import com.example.androiddevchallenge.util.removeLast

@Composable
fun AddScreen(viewModel: AddViewModel, modifier: Modifier, navigateToMain: (String) -> Unit) {
    AddScreenBody(viewModel.getColumns(), modifier = modifier)
}

@Composable
fun AddScreenBody(columns: List<List<String>>, modifier: Modifier = Modifier) {
    Column(modifier) {
        var textState by remember { mutableStateOf(EMPTY) }
        TimerValue(value = textState.fillWithZeros()) {
            textState = textState.removeLast()
        }
        Spacer(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(JettimerTheme.colors.searchBoxColor)
                .height(1.dp)
        )
        Dial(columns = columns) {
            if (textState.length < 6) textState += it
        }
    }
}

@Composable
fun TimerValue(value: String, onDelete: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        value.chunked(2).forEach {
            Text(text = it, modifier = Modifier.weight(1f))
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.label_delete)
            )
        }
    }
}

@Composable
fun Dial(columns: List<List<String>>, onItemClick: (String) -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Row {
            columns.forEach {
                Column {
                    it.forEach {
                        DialItem(value = it, onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
fun DialItem(value: String, onItemClick: (String) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(120.dp)
            .height(60.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 60.dp),
                onClick = { onItemClick(value) }
            )
    ) {
        Text(
            text = value,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4.copy(
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            ),
        )
    }
}

@Preview("Add screen body")
@Composable
fun PreviewAddScreenBody() {
    ThemedPreview {
        val columns = DataManager.columns
        AddScreenBody(columns = columns)
    }
}

@Preview("Add screen body dark")
@Composable
fun PreviewAddScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        val columns = DataManager.columns
        AddScreenBody(columns = columns)
    }
}
