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
package com.ericktijerou.jettimer.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularProgressWithThumb(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.secondaryVariant,
    backgroundColor: Color = Color.White,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth,
    thumbSize: Dp? = null
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
    }
    val thumbSizeInPx = with(LocalDensity.current) { thumbSize?.toPx() } ?: stroke.width
    Canvas(
        modifier
            .progressSemantics(progress)
            .size(40.dp)
            .focusable()
    ) {
        val startAngle = 270f
        val sweep = progress * 360f
        val diameterOffset = stroke.width / 2
        drawArcBackground(
            startAngle,
            (1 - progress) * 360,
            backgroundColor,
            stroke,
            diameterOffset
        )
        drawArcIndicator(startAngle, sweep, color, stroke, diameterOffset)
        drawThumb(thumbSizeInPx, color, diameterOffset, sweep, startAngle)
    }
}

fun DrawScope.drawThumb(
    dotSizeInPx: Float,
    color: Color,
    diameterOffset: Float,
    sweep: Float,
    startAngle: Float
) {
    val internalCenter = Offset(size.width / 2, size.width / 2)
    val radius = size.width / 2 - diameterOffset
    val mThumbX = (internalCenter.x + radius * cos(-(startAngle - sweep) * Math.PI / 180))
    val mThumbY = (internalCenter.y - radius * sin(-(startAngle - sweep) * Math.PI / 180))
    val middle = Offset(mThumbX.toFloat(), mThumbY.toFloat())
    drawCircle(color = color, radius = dotSizeInPx, center = middle)
}

fun DrawScope.drawArcBackground(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke,
    diameterOffset: Float
) {
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

fun DrawScope.drawArcIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke,
    diameterOffset: Float
) {
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = -sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}
