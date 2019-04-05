package com.evartem.invoiceman.util

import android.util.Log

fun Throwable.stackToString(): String = Log.getStackTraceString(this)