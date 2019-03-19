package com.evartem.invoiceman

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

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

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    companion object {
        private lateinit var INSTANCE: TheApp
        fun get() = INSTANCE
    }
}