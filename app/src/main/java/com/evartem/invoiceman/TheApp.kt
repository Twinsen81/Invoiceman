package com.evartem.invoiceman

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class TheApp : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

    companion object {
        private lateinit var INSTANCE: TheApp
        fun get() = INSTANCE
    }
}