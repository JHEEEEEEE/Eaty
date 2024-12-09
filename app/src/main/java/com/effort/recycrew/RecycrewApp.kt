package com.effort.recycrew

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class RecycrewApp: Application() {

    override fun onCreate() {
        super.onCreate()
    }

}