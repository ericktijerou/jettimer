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
import com.example.androiddevchallenge.countdown.TimerManager
import com.example.androiddevchallenge.manager.PreferenceManager
import com.example.androiddevchallenge.util.FIVE_HUNDRED
import com.example.androiddevchallenge.util.TimerState
import com.example.androiddevchallenge.util.ZERO_LONG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val timerManager: TimerManager,
    private val preferenceManager: PreferenceManager
) :
    ViewModel() {

    private var remainingTimeInMillis = ZERO_LONG
    private val _timerState = MutableLiveData<TimerState>()
    val timerState: LiveData<TimerState> = _timerState

    private var countDownJob = SupervisorJob()
    private var visibilityJob = SupervisorJob()

    private val _tick = MutableLiveData<Long>()
    val tick = Transformations.switchMap(_tick) {
        countDownJob = SupervisorJob()
        liveData(Dispatchers.Main + countDownJob) {
            timerManager.startCountDown(it).collect { tickInMillis ->
                remainingTimeInMillis = tickInMillis
                emit(tickInMillis)
                if (tickInMillis == ZERO_LONG) {
                    finishTimer()
                }
            }
        }
    }

    private val _timerVisibility = MutableLiveData<Boolean>()
    val timerVisibility = Transformations.switchMap(_timerVisibility) {
        visibilityJob = SupervisorJob()
        liveData(Dispatchers.Main + visibilityJob) {
            if (it) {
                emit(true)
            } else {
                timerManager.startPausedTimer(FIVE_HUNDRED).collect { tickInSeconds ->
                    emit(tickInSeconds)
                }
            }
        }
    }

    fun startTimer(millisUntilFinished: Long) {
        visibilityJob.cancel()
        countDownJob.cancel()
        _timerVisibility.value = true
        _tick.value = millisUntilFinished
        _timerState.value = TimerState.Started
    }

    private fun pauseTimer() {
        visibilityJob.cancel()
        countDownJob.cancel()
        _timerVisibility.value = false
        _timerState.value = TimerState.Paused
    }

    private fun finishTimer() {
        visibilityJob.cancel()
        _timerVisibility.value = false
        _timerState.value = TimerState.Finished
    }

    fun getTimer() = preferenceManager.timeInMillis

    fun clearTimer() {
        preferenceManager.timeInMillis = ZERO_LONG
    }

    fun onActionClick(currentState: TimerState, time: Long) {
        when (currentState) {
            TimerState.Started -> pauseTimer()
            TimerState.Stopped -> startTimer(time)
            TimerState.Paused -> startTimer(remainingTimeInMillis)
        }
    }
}
