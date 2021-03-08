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
package com.ericktijerou.jettimer

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ericktijerou.jettimer.manager.PreferenceManager
import com.ericktijerou.jettimer.service.startFinishedTimerService
import com.ericktijerou.jettimer.service.startTimerService
import com.ericktijerou.jettimer.service.stopFinishedTimerService
import com.ericktijerou.jettimer.service.stopTimerService
import com.ericktijerou.jettimer.util.TimerState
import dagger.hilt.android.HiltAndroidApp
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), LifecycleObserver {

    @Inject
    lateinit var preferences: PreferenceManager
    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        EventBus.getDefault().register(this)
        if (!preferences.isTimerRunning) {
            stopTimerService()
        }
    }

    override fun onTerminate() {
        EventBus.getDefault().unregister(this)
        super.onTerminate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        if (preferences.isTimerRunning) {
            startTimerService()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        stopTimerService()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Start) {
        preferences.isTimerRunning = true
        val delay = 0L
        val period = 250L
        timer = Timer()
        var interval = state.duration
        timer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    interval -= period
                    val newState = TimerState.Running(state.duration, interval)
                    EventBus.getDefault().post(newState)
                    if (interval == 0L) {
                        stopTimerService()
                        startFinishedTimerService()
                    }
                }
            },
            delay, period
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Finish) {
        preferences.isTimerRunning = false
        stopTimerService()
        stopFinishedTimerService()
        timer?.cancel()
    }
}
