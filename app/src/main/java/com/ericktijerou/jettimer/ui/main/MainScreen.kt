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
package com.ericktijerou.jettimer.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ericktijerou.jettimer.R
import com.ericktijerou.jettimer.ui.component.CircularProgressWithThumb
import com.ericktijerou.jettimer.ui.theme.JettimerTheme
import com.ericktijerou.jettimer.util.EMPTY
import com.ericktijerou.jettimer.util.ThemedPreview
import com.ericktijerou.jettimer.util.TimerState
import com.ericktijerou.jettimer.util.calculateFontSize
import com.ericktijerou.jettimer.util.isZero
import com.ericktijerou.jettimer.util.toHhMmSs
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier,
    autoPlay: Boolean,
    navigateToAdd: () -> Unit
) {
    val time = viewModel.getTimer()
    if (time.isZero()) {
        navigateToAdd()
        return
    }
    val (isFinish, setFinish) = remember { mutableStateOf(false) }
    val tick: Long by viewModel.tick.observeAsState(viewModel.getTempTimer())
    val timerLabel: String by viewModel.timerLabel.observeAsState(
        viewModel.getTempTimer().toHhMmSs()
    )
    val timerVisibility: Boolean by viewModel.timerVisibility.observeAsState(true)
    if (autoPlay) viewModel.startTimer()
    val timerScreenState: TimerState by viewModel.timerScreenState.observeAsState(
        TimerState.Stopped
    )
    BoxWithConstraints {
        val offsetY = with(LocalDensity.current) { maxHeight.toPx().toInt() / 2 }
        AnimatedVisibility(
            visible = !isFinish,
            exit = slideOutVertically(targetOffsetY = { -offsetY }) + fadeOut(),
            enter = slideInVertically(initialOffsetY = { -offsetY }),
            initiallyVisible = false
        ) {
            MainScreenBody(
                time = viewModel.getTempTimer(),
                tick = tick,
                timerLabel = timerLabel,
                modifier = modifier,
                timerScreenState = timerScreenState,
                timerVisibility = timerVisibility,
                onActionClick = { viewModel.onActionClick(timerScreenState) },
                onDelete = {
                    setFinish(true)
                },
                onOptionTimerClick = { viewModel.onOptionTimerClick(timerScreenState) }
            )
        }
    }
    LaunchedEffect(isFinish) {
        if (isFinish) {
            delay(100)
            viewModel.clearTimer()
            navigateToAdd()
        }
    }
}

@Composable
fun MainScreenBody(
    time: Long,
    tick: Long,
    timerLabel: String,
    modifier: Modifier = Modifier,
    timerScreenState: TimerState,
    timerVisibility: Boolean,
    onActionClick: () -> Unit,
    onDelete: () -> Unit,
    onOptionTimerClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .background(color = MaterialTheme.colors.primary)
            .fillMaxSize()
    ) {
        val (actionButtons, timer) = createRefs()
        val progress = (tick.toFloat() / time.toFloat()).coerceAtLeast(0f)
        val progressOffset = (1 - progress)
        val animatedProgress by animateFloatAsState(
            targetValue = progressOffset,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        MainTimer(
            animatedProgress = animatedProgress,
            formattedTime = timerLabel,
            timerScreenState = timerScreenState,
            timerVisibility = timerVisibility,
            modifier = Modifier
                .size(200.dp)
                .constrainAs(timer) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = actionButtons.top,
                        bottomMargin = 16.dp
                    )
                },
            onOptionTimerClick = onOptionTimerClick
        )

        ActionButtons(
            onActionClick = onActionClick,
            onDelete = onDelete,
            timerScreenState = timerScreenState,
            modifier = Modifier
                .constrainAs(actionButtons) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    linkTo(
                        start = parent.start,
                        end = parent.end
                    )
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainTimer(
    animatedProgress: Float,
    timerVisibility: Boolean,
    timerScreenState: TimerState,
    modifier: Modifier,
    formattedTime: String,
    onOptionTimerClick: () -> Unit
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val progressVisibility =
            if (timerScreenState == TimerState.Finished) timerVisibility else true
        if (progressVisibility) {
            CircularProgressWithThumb(
                progress = animatedProgress,
                strokeWidth = 4.dp,
                thumbSize = 6.dp,
                modifier = Modifier.fillMaxSize()
            )
        }

        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (label, timer, optionTimer) = createRefs()
            TextButton(
                onClick = { },
                enabled = false,
                modifier = Modifier.constrainAs(label) {
                    linkTo(start = parent.start, end = parent.end)
                    top.linkTo(parent.top, margin = 20.dp)
                }
            ) {
                Text(
                    text = stringResource(R.string.hint_label),
                    color = JettimerTheme.colors.textPrimaryColor.copy(alpha = ContentAlpha.disabled),
                    style = MaterialTheme.typography.body2.copy(
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.W400
                    )
                )
            }
            val labelTimerVisibility =
                if (timerScreenState == TimerState.Paused) timerVisibility else true
            AnimatedVisibility(
                visible = labelTimerVisibility,
                enter = fadeIn(initialAlpha = 0.6f),
                exit = fadeOut(),
                modifier = Modifier.constrainAs(timer) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom
                    )
                }
            ) {
                val color = if (timerScreenState == TimerState.Finished) {
                    JettimerTheme.colors.textPrimaryColor
                } else {
                    MaterialTheme.colors.secondaryVariant
                }
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.h2.copy(
                        fontSize = formattedTime.calculateFontSize(),
                        fontWeight = FontWeight.W400,
                        letterSpacing = 1.sp
                    ),
                    color = color,
                )
            }

            Button(
                onClick = onOptionTimerClick,
                elevation = null,
                modifier = Modifier.constrainAs(optionTimer) {
                    linkTo(start = parent.start, end = parent.end)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                }
            ) {
                val resId = when (timerScreenState) {
                    TimerState.Started, TimerState.Finished -> R.string.label_plus_one_minute
                    else -> R.string.label_reset
                }
                Text(
                    text = stringResource(resId),
                    style = MaterialTheme.typography.body2.copy(
                        letterSpacing = 0.sp,
                        fontWeight = FontWeight.W400
                    ),
                    color = JettimerTheme.colors.textPrimaryColor
                )
            }
        }
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier,
    timerScreenState: TimerState,
    onActionClick: () -> Unit,
    onDelete: () -> Unit
) {
    ConstraintLayout(modifier) {
        val (action, delete, addTimer) = createRefs()
        IconButton(
            onClick = onActionClick,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colors.secondaryVariant,
                    shape = CircleShape
                )
                .constrainAs(action) {
                    linkTo(
                        start = parent.start,
                        end = parent.end
                    )
                }
        ) {
            val icon = when (timerScreenState) {
                TimerState.Started -> Icons.Outlined.Pause
                TimerState.Paused, TimerState.Stopped -> Icons.Outlined.PlayArrow
                else -> Icons.Outlined.Stop
            }
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.label_start),
                tint = JettimerTheme.colors.textVariantColor
            )
        }
        Button(
            onClick = onDelete,
            elevation = null,
            modifier = Modifier.constrainAs(delete) {
                linkTo(top = action.top, bottom = action.bottom)
                start.linkTo(parent.start, margin = 16.dp)
            }
        ) {
            Text(
                text = stringResource(R.string.label_delete),
                style = MaterialTheme.typography.body2.copy(
                    letterSpacing = 0.sp,
                    fontWeight = FontWeight.W400
                ),
                color = JettimerTheme.colors.textPrimaryColor
            )
        }
        TextButton(
            onClick = {},
            enabled = false,
            modifier = Modifier
                .constrainAs(addTimer) {
                    linkTo(top = action.top, bottom = action.bottom)
                    end.linkTo(parent.end, margin = 16.dp)
                }
        ) {
            Text(
                text = stringResource(R.string.label_add_timer),
                color = JettimerTheme.colors.textPrimaryColor.copy(alpha = ContentAlpha.disabled),
                style = MaterialTheme.typography.body2.copy(
                    letterSpacing = 0.sp,
                    fontWeight = FontWeight.W400
                )
            )
        }
    }
}

@Preview("Main screen body")
@Composable
fun PreviewHomeScreenBody() {
    ThemedPreview {
        MainScreenBody(
            time = 36000,
            tick = 3000,
            timerScreenState = TimerState.Started,
            timerVisibility = true,
            onActionClick = {},
            onDelete = {},
            onOptionTimerClick = {},
            timerLabel = EMPTY
        )
    }
}

@Preview("Main screen body dark")
@Composable
fun PreviewHomeScreenBodyDark() {
    ThemedPreview {
        MainScreenBody(
            time = 36000,
            tick = 3000,
            timerScreenState = TimerState.Started,
            timerVisibility = true,
            onActionClick = {},
            onDelete = {},
            onOptionTimerClick = {},
            timerLabel = EMPTY
        )
    }
}
