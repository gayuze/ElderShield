package com.eldershield

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ElderShieldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
