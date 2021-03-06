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
package com.ericktijerou.jettimer.ui.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ericktijerou.jettimer.R
import com.ericktijerou.jettimer.manager.DataManager
import com.ericktijerou.jettimer.ui.component.StartButton
import com.ericktijerou.jettimer.ui.theme.JettimerTheme
import com.ericktijerou.jettimer.util.EMPTY
import com.ericktijerou.jettimer.util.ThemedPreview
import com.ericktijerou.jettimer.util.fillWithZeros
import com.ericktijerou.jettimer.util.firstInputIsZero
import com.ericktijerou.jettimer.util.removeLast
import com.ericktijerou.jettimer.util.toMillis
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddScreen(viewModel: AddViewModel, modifier: Modifier, navigateToMain: () -> Unit) {
    val (isFinish, setFinish) = remember { mutableStateOf(false) }
    BoxWithConstraints {
        val offsetY = with(LocalDensity.current) { maxHeight.toPx().toInt() / 2 }
        AnimatedVisibility(
            visible = !isFinish,
            exit = slideOutVertically(targetOffsetY = { offsetY }) + fadeOut(),
            enter = slideInVertically(initialOffsetY = { offsetY }),
            initiallyVisible = false
        ) {
            AddScreenBody(
                timeUnits = viewModel.getTimeUnits(),
                dialColumns = viewModel.getColumns(),
                modifier = modifier,
                navigateToMain = {
                    viewModel.setTimer(it)
                    setFinish(true)
                }
            )
        }
    }
    LaunchedEffect(isFinish) {
        if (isFinish) {
            delay(100)
            navigateToMain()
        }
    }
}

@Composable
fun AddScreenBody(
    timeUnits: List<String>,
    dialColumns: List<List<String>>,
    modifier: Modifier = Modifier,
    navigateToMain: (Long) -> Unit
) {
    Column(
        modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxSize()
    ) {
        var textState by remember { mutableStateOf(EMPTY) }
        TimerValue(
            value = textState.fillWithZeros(),
            timeUnits = timeUnits,
            enabled = textState.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, bottom = 36.dp),
            onDelete = { textState = textState.removeLast() },
            onDeleteAll = { textState = EMPTY }
        )
        Spacer(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
                .background(JettimerTheme.colors.searchBoxColor)
                .height(1.5.dp)
        )
        Dial(columns = dialColumns) {
            if (textState.length < 6 && !textState.firstInputIsZero(it)) textState += it
        }
        StartButton(
            visible = textState.isNotEmpty(),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) { navigateToMain(textState.toMillis()) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerValue(
    value: String,
    enabled: Boolean,
    modifier: Modifier,
    timeUnits: List<String>,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        value.chunked(2).forEachIndexed { index, s ->
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                val color =
                    if (enabled) MaterialTheme.colors.secondaryVariant else JettimerTheme.colors.textSecondaryColor
                Text(
                    text = s,
                    style = MaterialTheme.typography.h3.copy(
                        fontWeight = FontWeight.W400,
                        letterSpacing = 1.sp
                    ),
                    color = color
                )
                Text(
                    text = timeUnits[index],
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.W500),
                    modifier = Modifier.padding(bottom = 6.dp),
                    color = color
                )
            }
        }

        Icon(
            imageVector = Icons.Outlined.Backspace,
            contentDescription = stringResource(R.string.label_delete),
            tint = if (enabled) JettimerTheme.colors.textPrimaryColor else JettimerTheme.colors.textSecondaryColor,
            modifier = Modifier
                .padding(start = 8.dp)
                .combinedClickable(
                    onClick = onDelete,
                    onLongClick = onDeleteAll,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 32.dp)
                )
        )
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
            .height(85.dp)
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
            ),
        )
    }
}

@Preview("Add screen body")
@Composable
fun PreviewAddScreenBody() {
    ThemedPreview {
        AddScreenBody(
            timeUnits = DataManager.timeUnits,
            dialColumns = DataManager.columns
        ) {}
    }
}

@Preview("Add screen body dark")
@Composable
fun PreviewAddScreenBodyDark() {
    ThemedPreview {
        AddScreenBody(
            timeUnits = DataManager.timeUnits,
            dialColumns = DataManager.columns
        ) {}
    }
}
