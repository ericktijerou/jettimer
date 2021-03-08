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
package com.example.androiddevchallenge.util

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.example.androiddevchallenge.MainActivity
import com.example.androiddevchallenge.manager.PreferenceManager
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

fun String.fillWithZeros() = this.padStart(MAX_LENGTH_TIMER, ZERO_STRING.first())
fun String.removeLast() = if (isNotEmpty()) this.take(this.length - 1) else this
fun String.firstInputIsZero(input: String) = this.isEmpty() && input == ZERO_STRING
fun Long.isNotZero(): Boolean = this != ZERO_LONG
fun Long?.getPositiveValue(): Long = this?.let { if (this < 0) ZERO_LONG else this } ?: ZERO_LONG
fun Long.isZero(): Boolean = this == ZERO_LONG
fun Int.isZero(): Boolean = this == ZERO_INT

fun String.toMillis(): Long {
    var timeInMillis = 0L
    this.fillWithZeros().chunked(2).fastForEachIndexed { i, s ->
        when (i) {
            0 -> timeInMillis += TimeUnit.HOURS.toMillis(s.toLong())
            1 -> timeInMillis += TimeUnit.MINUTES.toMillis(s.toLong())
            2 -> timeInMillis += TimeUnit.SECONDS.toMillis(s.toLong())
        }
    }
    return timeInMillis
}

fun Float.roundUp(): Long = this.toBigDecimal().setScale(0, BigDecimal.ROUND_UP).longValueExact()

fun Int.toStringOrEmpty(): String = if (this.isZero()) EMPTY else this.toString()
fun Int.toFormattedString(): String =
    if (this in 9 downTo -9) "$ZERO_STRING${this.absoluteValue}" else this.toStringOrEmpty()

fun Int.minuteToString(hasHour: Boolean): String =
    if (hasHour) this.toFormattedString() else this.toStringOrEmpty()

fun Int.secondToString(hasMinute: Boolean): String =
    if (hasMinute) this.toFormattedString() else this.toString()

fun String.removeExtraColon(): String =
    if (this.first().toString() == COLON) takeLast(length - 1) else this

fun Long.toHhMmSs(): String {
    val hours = ((this / (1000 * 60 * 60) % 24)).toInt().toStringOrEmpty()
    val minutes = ((this / (1000 * 60) % 60)).toInt().minuteToString(hours.isNotEmpty())
    val seconds = ((this / 1000) % 60).toInt().secondToString(minutes.isNotEmpty())
    var formattedTime = "$hours$COLON$minutes$COLON$seconds"
    while (formattedTime.isNotEmpty() && formattedTime.first().toString() == COLON) {
        formattedTime = formattedTime.removeExtraColon()
    }
    return formattedTime
}

fun String.calculateFontSize(): TextUnit {
    return when (length) {
        8 -> 40.sp
        7 -> 48.sp
        5 -> 64.sp
        else -> 72.sp
    }
}

val Context.preferences: PreferenceManager get() = PreferenceManager(this)

fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun Context.getLaunchIntent() =
    packageManager.getLaunchIntentForPackage("com.ericktijerou.jettimer")

@SuppressLint("UnspecifiedImmutableFlag")
fun Context.getOpenTimerTabIntent(): PendingIntent {
    val intent = getLaunchIntent() ?: Intent(this, MainActivity::class.java)
    return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}
