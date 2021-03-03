package com.example.androiddevchallenge.countdown

import android.os.CountDownTimer
import android.os.Looper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@OptIn(ExperimentalCoroutinesApi::class)
class CountDownManagerImpl : CountDownManager {
    override fun start(millisUntilFinished: Long) = callbackFlow<Long> {
        if (Looper.myLooper() == null) {
            throw IllegalStateException("Can't create TimerFlow inside thread that has not called Looper.prepare() Just use Dispatchers.Main")
        }
        val timer = object : CountDownTimer(millisUntilFinished, 1000) {
            override fun onFinish() {
                offer(0L)
                cancel()
            }

            override fun onTick(millisUntilFinished: Long) {
                offer(millisUntilFinished)
            }
        }
        timer.start()
        awaitClose{ timer.cancel() }
    }
}