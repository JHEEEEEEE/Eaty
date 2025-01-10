package com.effort.remote.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Singleton 범위에서 Remote Layer에만 주입
object RemoteModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}