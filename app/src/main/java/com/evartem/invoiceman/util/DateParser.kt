package com.evartem.invoiceman.util

import java.text.SimpleDateFormat
import java.util.*

object DateParser {
    private val parser = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun toLocalDate(dateString: String): Date =
        try {
            parser.parse(dateString)
        } catch (ignored: Throwable) {
            Date()
        }
}