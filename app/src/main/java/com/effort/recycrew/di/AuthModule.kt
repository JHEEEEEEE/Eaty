@file:Suppress("DEPRECATION")
package com.effort.recycrew.di

import android.content.Context
import com.effort.recycrew.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        Timber.d("GoogleSignInClient 제공 시작")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_WEB_CLIENT_ID)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        Timber.d("GoogleSignInClient 제공 완료")
        return client
    }
}
