package com.evartem.invoiceman.invoices.mvi.util

import timber.log.Timber

class TimberConsoleTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        println("${tag ?: ""} $message ${t?.message ?: ""}")
    }
}