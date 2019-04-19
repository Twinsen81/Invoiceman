package com.evartem.invoiceman.util

import android.content.res.Resources
import android.util.Log

fun Throwable.stackToString(): String = Log.getStackTraceString(this)

fun Int.toString(resources: Resources): String = resources.getString(this)