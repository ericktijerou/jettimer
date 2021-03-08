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
    private val preferenceManager: PreferenceManager
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
    private val _timerState = MutableLiveData<TimerState>()
    val timerScreenState: LiveData<TimerState> = _timerState

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
        EventBus.getDefault().post(TimerState.Finish)
        visibilityJob.cancel()
        _timerLabel.value = millisUntilFinished.toHhMmSs()
        _timerVisibility.value = true
        EventBus.getDefault().post(TimerState.Start(millisUntilFinished))
        _timerState.value = TimerState.Started
    }

    fun startTimer() {
        EventBus.getDefault().post(TimerState.Finish)
        visibilityJob.cancel()
        _timerVisibility.value = true
        EventBus.getDefault().post(TimerState.Start(getTempTimer()))
        _timerState.value = TimerState.Started
    }

    private fun pauseTimer() {
        visibilityJob.cancel()
        EventBus.getDefault().post(TimerState.Finish)
        _timerVisibility.value = false
        _timerState.value = TimerState.Paused
    }

    private fun reset() {
        onFinish()
    }

    fun getTimer() = preferenceManager.timeInMillis

    private fun setTempTimer(value: Long) {
        preferenceManager.tempTimeInMillis = value
    }

    fun getTempTimer() = preferenceManager.tempTimeInMillis.coerceAtLeast(getTimer())

    fun clearTimer() {
        visibilityJob.cancel()
        EventBus.getDefault().post(TimerState.Finish)
        preferenceManager.tempTimeInMillis = ZERO_LONG
        preferenceManager.timeInMillis = ZERO_LONG
    }

    fun onActionClick(currentScreenState: TimerState) {
        when (currentScreenState) {
            TimerState.Started -> pauseTimer()
            TimerState.Stopped -> startTimer()
            TimerState.Paused -> resumeTimer(remainingTimeInMillis)
            TimerState.Finished -> reset()
        }
    }

    private fun onFinish() {
        EventBus.getDefault().post(TimerState.Finish)
        onFinishedState()
    }

    private fun onFinishedState() {
        visibilityJob.cancel()
        preferenceManager.tempTimeInMillis = ZERO_LONG
        _timerLabel.value = getTimer().toHhMmSs()
        _timerVisibility.value = true
        _tick.value = getTimer()
        _timerState.value = TimerState.Stopped
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Finished) {
        onFinishedState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Running) {
        remainingTimeInMillis = state.tick
        _tick.value = state.tick
        if (state.tick % 1000 == 0L) _timerLabel.value = state.tick.toHhMmSs()
        if (state.tick <= ZERO_LONG) {
            if (_timerVisibility.value != false) _timerVisibility.value = false
            if (_timerState.value != TimerState.Finished) _timerState.value = TimerState.Finished
        } else {
            if (_timerState.value != TimerState.Started) _timerState.value = TimerState.Started
        }
    }

    private fun getElapsedTime(currentTime: Long): Long {
        return if (remainingTimeInMillis > 0) currentTime - remainingTimeInMillis else ZERO_LONG
    }

    fun onOptionTimerClick(currentScreenState: TimerState) {
        when (currentScreenState) {
            TimerState.Started, TimerState.Finished -> {
                val currentTime = getTempTimer()
                setTempTimer(tick.value.getPositiveValue() + 60000 + getElapsedTime(currentTime = currentTime))
                resumeTimer(getTempTimer() - getElapsedTime(currentTime = currentTime))
            }
            else -> reset()
        }
    }
}
