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

import android.os.CountDownTimer
import com.example.androiddevchallenge.util.ONE_HUNDRED_FLOAT
import com.example.androiddevchallenge.util.ONE_HUNDRED_LONG
import com.example.androiddevchallenge.util.roundUp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalCoroutinesApi::class)
class CountDownManagerImpl : CountDownManager {
    override fun start(millisUntilFinished: Long) = callbackFlow<Long> {
        val timer = object : CountDownTimer(millisUntilFinished, ONE_HUNDRED_LONG) {
            override fun onFinish() {
                offer(0L)
                cancel()
            }
            override fun onTick(millisUntilFinished: Long) {
                val remainingTimeInSeconds = (millisUntilFinished / ONE_HUNDRED_FLOAT).roundUp()
                offer(remainingTimeInSeconds)
            }
        }
        timer.start()
        awaitClose { timer.cancel() }
    }
}
