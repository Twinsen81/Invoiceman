package com.evartem.backendsim

import com.evartem.backendsim.RandomInvoiceGenerator.getInvoices
import com.evartem.domain.entity.doc.Invoice
import java.util.*

object RandomInvoiceGenerator {

    fun getInvoices(number: Int): List<Invoice> =
        (1..number).map { getInvoice() }

    private fun getInvoice(): Invoice {
        val invProducts = RandomProductGenerator.getProducts((3..10).random())
        val invNumber = number
        val invDate = date
        val invSeller = seller
        return Invoice(
            UUID.randomUUID().toString(),
            invNumber,
            invDate,
            invSeller,
            RandomProductGenerator.getProducts((3..10).random()),
            null,
            getScanCopyUrl(invSeller, invNumber, invDate, invProducts.size),
            comment
        )
    }

    private val date get() = "%02d.%02d.%d".format((1..31).random(), (1..12).random(), (2018..2019).random())

    private val seller
        get() = listOf(
            "New Tech Inc.", "Industrial Solutions LLC", "ACME Industries", "UnH Solutions", "SquareWheel Inc."
        ).let {
            it[(0 until it.size).random()]
        }

    private val number get() = (1..100).random()

    private val comment
        get() = listOf(
            "Urgent!", "Call Ann before you begin", "Double check the condition", "Two packagings may be unsealed",
            "Do not accept if unsealed!", "Very fragile! Be careful!", "Send to block B for safekeeping", null
        ).let { it[(0 until it.size).random()] }

    private fun getScanCopyUrl(seller: String, number: Int, date: String, numberOfProducts: Int) = listOf(
        "https://via.placeholder.com/640x360/DADADA/000000?text=Invoice+%23$number+$date%0D%0ASeller%3A" +
                "+${seller.replace(" ", "+")}%0D%0AProducts%3A+$numberOfProducts",
        null
    ).let { it[(0 until it.size).random()] }
}

fun main() {
    print(getInvoices(1))
}