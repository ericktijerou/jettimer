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
package com.example.androiddevchallenge.countdown

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.Timer
import java.util.TimerTask

@OptIn(ExperimentalCoroutinesApi::class)
class TimerManagerImpl : TimerManager {
    override fun startCountDown(millisUntilFinished: Long) = callbackFlow<Long> {
        val delay = 0L
        val period = 100L
        val timer = Timer()
        var interval = millisUntilFinished
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    interval -= period
                    offer(interval)
                }
            },
            delay, period
        )
        awaitClose { timer.cancel() }
    }

    override fun startPausedTimer(period: Long) = callbackFlow<Boolean> {
        val timer = Timer()
        var time = true
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try { offer(time) } catch (e: Exception) {}
                    time = !time
                }
            },
            0,
            period
        )
        awaitClose { timer.cancel() }
    }
}
