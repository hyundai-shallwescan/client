package com.ite.sws

import android.app.Application
import com.ite.sws.util.SharedPreferencesUtil

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesUtil.init(this)
    }
}