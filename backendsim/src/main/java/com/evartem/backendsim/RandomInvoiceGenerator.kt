package com.evartem.backendsim

import com.evartem.domain.entity.doc.Invoice

object RandomInvoiceGenerator {

    fun MutableSet<String>.pop(): String {
        val element = if (size > 0) elementAt(0) else ""
        if (element.isNotBlank()) remove(element)
        return element
    }

    fun getInvoices(number: Int): List<Invoice> {
        val allIds = ids
        val randomIds = allIds.shuffled().take(if (number >= allIds.size) allIds.size else number).toMutableSet()
        return (1..number).map { getInvoice(randomIds.pop()) }
    }

    private fun getInvoice(id: String): Invoice {
        val invProducts = RandomProductGenerator.getProducts((2..8).random())
        val invNumber = number
        val invDate = date
        val invSeller = seller
        return Invoice(
            id,
            invNumber,
            invDate,
            invSeller,
            invProducts,
            null,
            getScanCopyUrl(invSeller, invNumber, invDate, invProducts.size),
            comment
        )
    }

    private val ids
        get() = listOf(
            "903ae97d-9f5a-4ae4-bbf6-bc5be1a8f7b6",
            "73c986d5-6799-4c5b-b550-3db4d71f11ed",
            "3e787275-e35c-464a-b5b3-20ac51a5d314",
            "212c833d-dd1e-4f5c-a389-ff6b6aee42bb",
            "b0bb816a-d8ec-4bd6-913c-9abb2fe64366",
            "979c590f-22e4-4fab-b037-2422854d3993",
            "9a516a35-d8c2-4d06-899b-a5f176b38024",
            "9d4b5eb3-ceab-4f91-8a13-ce5ec55aabed",
            "9cd34ca2-40f6-4143-a679-a06424ac5be3",
            "6e0ce630-e869-4c3e-a872-0584e23a2879"
        )

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