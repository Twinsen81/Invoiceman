package com.evartem.invoiceman

import android.app.Application

class TheApp: Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        private lateinit var INSTANCE: TheApp
        fun get() = INSTANCE
    }
}