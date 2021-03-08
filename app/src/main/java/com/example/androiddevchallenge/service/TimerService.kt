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
package com.example.androiddevchallenge.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.util.TIMER_RUNNING_ID
import com.example.androiddevchallenge.util.TimerState
import com.example.androiddevchallenge.util.getOpenTimerTabIntent
import com.example.androiddevchallenge.util.isOreoPlus
import com.example.androiddevchallenge.util.toHhMmSs
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class TimerService : Service() {

    private val bus = EventBus.getDefault()

    override fun onCreate() {
        super.onCreate()
        bus.register(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground(TIMER_RUNNING_ID, notification(""))
        return START_NOT_STICKY
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: TimerState.Running) {
        if (state.tick % 1000 == 0L) {
            val notification: Notification = notification(state.tick.toHhMmSs())
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(TIMER_RUNNING_ID, notification)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TimerStopService) {
        stopService()
    }

    private fun stopService() {
        if (isOreoPlus()) {
            stopForeground(true)
        } else {
            stopSelf()
        }
        onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
    }

    private fun notification(formattedTick: String): Notification {
        val channelId = "simple_alarm_timer"
        val label = getString(R.string.app_name)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_LOW
            NotificationChannel(channelId, label, importance).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this)
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(formattedTick)
            .setContentText(getString(R.string.label_timer))
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(this.getOpenTimerTabIntent())
            .setSound(null)
            .setOngoing(true)
            .setAutoCancel(true)
            .setChannelId(channelId)

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return builder.build()
    }
}

fun startTimerService(context: Context) {
    if (isOreoPlus()) {
        context.startForegroundService(Intent(context, TimerService::class.java))
    } else {
        context.startService(Intent(context, TimerService::class.java))
    }
}

fun finishTimerService(context: Context) {
    context.stopService(Intent(context, TimerService::class.java))
}

object TimerStopService
