package com.effort.recycrew

import android.app.Application
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.conscrypt.Conscrypt
import java.security.Security


@HiltAndroidApp
class RecycrewApp : Application() {
    companion object {
        private lateinit var recycrewApp: RecycrewApp
        fun getRecycrewApp() = recycrewApp
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        recycrewApp = this
        Security.insertProviderAt(Conscrypt.newProvider(), 1)

        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}