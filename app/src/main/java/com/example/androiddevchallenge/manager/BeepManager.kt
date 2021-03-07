package com.example.androiddevchallenge.manager

import android.app.Service
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.example.androiddevchallenge.R
import javax.inject.Inject


class BeepManager @Inject constructor(val context: Context) {

    private var player = newPlayer()

    fun vibrate(durationMill: Long = VIBRATE_DURATION) {
        val vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    durationMill,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator?.vibrate(durationMill)
        }
    }

    private fun newPlayer(): MediaPlayer {
        return MediaPlayer.create(context, R.raw.beep_alarm)

    }

    fun playDefaultNotificationSound() {
        player = newPlayer()
        player.isLooping = true
        player.start()
    }

    fun stopNotificationSound() {
        player.stop()
        player.reset()
    }

    fun vibrateWave(
        pattern: LongArray = longArrayOf(
            VIBRATE_WAVE_DURATION,
            VIBRATE_WAVE_DURATION,
            VIBRATE_WAVE_DURATION,
            VIBRATE_WAVE_DURATION
        ),
        isRepeat: Boolean = false
    ) {
        val vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, if (isRepeat) 1 else -1))
        } else {
            vibrator?.vibrate(pattern, if (isRepeat) 1 else -1)
        }
    }


    companion object {
        private const val VIBRATE_DURATION = 100L
        private const val VIBRATE_WAVE_DURATION = 130L
    }
}