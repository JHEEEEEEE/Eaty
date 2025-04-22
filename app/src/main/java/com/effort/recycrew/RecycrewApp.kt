package com.effort.recycrew

import android.app.Application
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RecycrewApp : Application() {
    private lateinit var recycrewApp: RecycrewApp

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        recycrewApp = this

        Timber.plant(Timber.DebugTree())

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}