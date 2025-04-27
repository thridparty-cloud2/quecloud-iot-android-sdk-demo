package com.quectel.app.demo

import androidx.multidex.MultiDexApplication

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        SdkManager.init(this)
    }
}
