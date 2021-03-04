package com.example.androiddevchallenge.ui.component

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
import com.example.androiddevchallenge.ui.theme.JettimerTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Timer(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.secondaryVariant,
    backgroundColor: Color = JettimerTheme.colors.textSecondaryColor,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
    }
    val dotSizeInPx = stroke.width
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
        drawThumb(dotSizeInPx, color, diameterOffset, sweep, startAngle)
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
    val mThumbX = (internalCenter.x + radius * cos(-(sweep + startAngle) * Math.PI / 180))
    val mThumbY = (internalCenter.y - radius * sin(-(sweep + startAngle) * Math.PI / 180))
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
        sweepAngle = -sweep,
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
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}