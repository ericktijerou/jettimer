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
package com.example.androiddevchallenge

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.androiddevchallenge.service.FinishedTimerStopService
import com.example.androiddevchallenge.service.TimerStopService
import com.example.androiddevchallenge.service.finishTimerService
import com.example.androiddevchallenge.service.startFinishedTimerService
import com.example.androiddevchallenge.service.startTimerService
import com.example.androiddevchallenge.service.stopFinishedTimerService
import com.example.androiddevchallenge.util.TimerState
import com.example.androiddevchallenge.util.preferences
import dagger.hilt.android.HiltAndroidApp
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Timer
import java.util.TimerTask

@HiltAndroidApp
class App : Application(), LifecycleObserver {

    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        EventBus.getDefault().register(this)
    }

    override fun onTerminate() {
        EventBus.getDefault().unregister(this)
        super.onTerminate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        if (preferences.timeInMillis != 0L) {
            startTimerService(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Start) {
        val delay = 0L
        val period = 100L
        timer = Timer()
        var interval = state.duration
        timer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    interval -= period
                    val newState = TimerState.Running(state.duration, interval)
                    EventBus.getDefault().post(newState)
                    if (interval == 0L) {
                        EventBus.getDefault().post(TimerState.Finish(interval))
                        EventBus.getDefault().post(TimerStopService)
                        startFinishedTimerService(this@App)
                    }
                }
            },
            delay, period
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Finished) {
        timer?.cancel()
        EventBus.getDefault().post(TimerStopService)
        EventBus.getDefault().post(FinishedTimerStopService)
        stopFinishedTimerService(this)
        finishTimerService(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        EventBus.getDefault().post(TimerStopService)
    }
}
