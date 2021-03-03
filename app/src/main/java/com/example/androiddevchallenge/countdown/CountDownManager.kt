package com.example.androiddevchallenge.countdown

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface CountDownManager {
    fun start(millisUntilFinished: Long): Flow<Long>
}