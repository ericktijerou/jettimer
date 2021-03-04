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
package com.example.androiddevchallenge.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.theme.JettimerTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartButton(visible: Boolean, modifier: Modifier, onClick: () -> Unit) {
    BoxWithConstraints(contentAlignment = Alignment.Center, modifier = modifier) {
        val height = with(LocalDensity.current) { maxHeight.toPx().toInt() }
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = { height }),
            exit = slideOutVertically(targetOffsetY = { height })
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colors.secondaryVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = stringResource(R.string.label_start),
                    tint = JettimerTheme.colors.textVariantColor
                )
            }
        }
    }
}
