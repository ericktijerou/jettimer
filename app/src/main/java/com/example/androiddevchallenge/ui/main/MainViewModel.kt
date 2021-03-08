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
package com.example.androiddevchallenge.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.androiddevchallenge.countdown.IntermittentTimerManager
import com.example.androiddevchallenge.manager.BeepManager
import com.example.androiddevchallenge.manager.PreferenceManager
import com.example.androiddevchallenge.util.TimerScreenState
import com.example.androiddevchallenge.util.TimerState
import com.example.androiddevchallenge.util.ZERO_LONG
import com.example.androiddevchallenge.util.getPositiveValue
import com.example.androiddevchallenge.util.toHhMmSs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val intermittentTimerManager: IntermittentTimerManager,
    private val preferenceManager: PreferenceManager,
    private val beepManager: BeepManager
) :
    ViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    private val _timerLabel = MutableLiveData<String>()
    val timerLabel: LiveData<String> = _timerLabel

    private var remainingTimeInMillis = ZERO_LONG
    private val _timerState = MutableLiveData<TimerScreenState>()
    val timerScreenState: LiveData<TimerScreenState> = _timerState

    private var visibilityJob = SupervisorJob()

    private val _tick = MutableLiveData<Long>()
    val tick: LiveData<Long> = _tick

    private val _timerVisibility = MutableLiveData<Boolean>()
    val timerVisibility = Transformations.switchMap(_timerVisibility) {
        visibilityJob = SupervisorJob()
        liveData(Dispatchers.IO + visibilityJob) {
            if (it) {
                emit(true)
            } else {
                intermittentTimerManager.startIntermittentTimer().collect { tickInSeconds ->
                    emit(tickInSeconds)
                }
            }
        }
    }

    private fun resumeTimer(millisUntilFinished: Long) {
        visibilityJob.cancel()
        EventBus.getDefault().post(TimerState.Finished)
        _timerLabel.value = millisUntilFinished.toHhMmSs()
        _timerVisibility.value = true
        EventBus.getDefault().post(TimerState.Start(millisUntilFinished))
        _timerState.value = TimerScreenState.Started
    }

    fun startTimer() {
        visibilityJob.cancel()
        EventBus.getDefault().post(TimerState.Finished)
        _timerVisibility.value = true
        EventBus.getDefault().post(TimerState.Start(getTempTimer()))
        _timerState.value = TimerScreenState.Started
    }

    private fun pauseTimer() {
        visibilityJob.cancel()
        EventBus.getDefault().post(TimerState.Finished)
        _timerVisibility.value = false
        _timerState.value = TimerScreenState.Paused
    }

    private fun finishTimer() {
        visibilityJob.cancel()
        _timerVisibility.value = false
        _timerState.value = TimerScreenState.Finished
        beepManager.vibrateWave()
        beepManager.playDefaultNotificationSound()
    }

    private fun reset() {
        EventBus.getDefault().post(TimerState.Finished)
    }

    fun getTimer() = preferenceManager.timeInMillis

    private fun setTempTimer(value: Long) {
        preferenceManager.tempTimeInMillis = value
    }

    fun getTempTimer() = preferenceManager.tempTimeInMillis.coerceAtLeast(getTimer())

    fun clearTimer() {
        visibilityJob.cancel()
        EventBus.getDefault().post(TimerState.Finished)
        beepManager.stopNotificationSound()
        preferenceManager.tempTimeInMillis = ZERO_LONG
        preferenceManager.timeInMillis = ZERO_LONG
    }

    fun onActionClick(currentScreenState: TimerScreenState) {
        when (currentScreenState) {
            TimerScreenState.Started -> pauseTimer()
            TimerScreenState.Stopped -> startTimer()
            TimerScreenState.Paused -> resumeTimer(remainingTimeInMillis)
            TimerScreenState.Finished -> reset()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Finished) {
        beepManager.stopNotificationSound()
        visibilityJob.cancel()
        preferenceManager.tempTimeInMillis = ZERO_LONG
        _timerLabel.value = getTimer().toHhMmSs()
        _timerVisibility.value = true
        _tick.value = getTimer()
        _timerState.value = TimerScreenState.Stopped
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Running) {
        remainingTimeInMillis = state.tick
        _tick.value = state.tick
        if (state.tick % 1000 == 0L) _timerLabel.value = state.tick.toHhMmSs()
        if (state.tick <= ZERO_LONG) {
            if (_timerVisibility.value != false) _timerVisibility.value = false
            if (_timerState.value != TimerScreenState.Finished) _timerState.value = TimerScreenState.Finished
        }
    }

    private fun getElapsedTime(currentTime: Long): Long {
        return if (remainingTimeInMillis > 0) currentTime - remainingTimeInMillis else ZERO_LONG
    }

    fun onOptionTimerClick(currentScreenState: TimerScreenState) {
        when (currentScreenState) {
            TimerScreenState.Started, TimerScreenState.Finished -> {
                val currentTime = getTempTimer()
                setTempTimer(tick.value.getPositiveValue() + 60000 + getElapsedTime(currentTime = currentTime))
                resumeTimer(getTempTimer() - getElapsedTime(currentTime = currentTime))
            }
            TimerScreenState.Stopped, TimerScreenState.Paused -> reset()
        }
    }
}
