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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.androiddevchallenge.countdown.CountDownManager
import com.example.androiddevchallenge.manager.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val countDownManager: CountDownManager,
    private val preferenceManager: PreferenceManager
) :
    ViewModel() {

    private val _tick = MutableLiveData<Long>(0)
    private val result = Transformations.map(_tick) { countDownManager.start(it) }
    val tick = result.switchMap { it.asLiveData() }

    fun start(millisUntilFinished: Long) {
        _tick.postValue(millisUntilFinished)
    }

    fun getTimer() = preferenceManager.timeInMillis

    fun clearTimer() {
        preferenceManager.timeInMillis = 0
    }
}
