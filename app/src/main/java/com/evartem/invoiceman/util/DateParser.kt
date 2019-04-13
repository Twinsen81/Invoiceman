package com.evartem.invoiceman.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Convert a date from a string representation to the [Date] object to allow sorting invoices by date.
 */
object DateParser {
    @SuppressLint("ConstantLocale")
    private val parser = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun toLocalDate(dateString: String): Date =
        try {
            parser.parse(dateString)
        } catch (ignored: Throwable) {
            Date()
        }
}