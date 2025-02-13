package com.effort.remote.di

import android.content.Context
import com.effort.remote.service.share.ShareService
import com.effort.remote.service.share.ShareServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShareModule {

    @Provides
    @Singleton
    fun provideShareService(
        @ApplicationContext context: Context,
    ): ShareService {
        return ShareServiceImpl(context)
    }
}