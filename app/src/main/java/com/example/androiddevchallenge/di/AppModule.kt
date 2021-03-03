package com.example.androiddevchallenge.di

import android.content.Context
import com.example.androiddevchallenge.countdown.CountDownManager
import com.example.androiddevchallenge.countdown.CountDownManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideRepoRepository(): CountDownManager {
        return CountDownManagerImpl()
    }
}