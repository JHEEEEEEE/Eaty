package com.effort.recycrew

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class RecycrewApp: Application() {
    companion object {
        private lateinit var recycrewApp: RecycrewApp
        fun getRecycrewApp() = recycrewApp
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        recycrewApp = this
    }
}