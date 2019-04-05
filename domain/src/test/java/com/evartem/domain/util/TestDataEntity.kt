package com.evartem.domain.util

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Product
import com.evartem.domain.entity.doc.ResultStatus

class TestDataEntity {

    lateinit var invoice1: Invoice
    lateinit var invoice2: Invoice

    val user = User("test@test.com")

    init {
        createTestInvoice1()
        createTestInvoice2()
    }

    private fun createTestInvoice1() {

        val product1 = Product(1, "6ES7322-1BL00-0AA0", "32 DI module", 3)
        val product2 =
            Product(
                2, "6ES7322-1BL00-0AA0", "32 DI module", 2,
                serialNumberPattern = "S[A-Z\\d]{10}"
            )
        val product1SerialNumber1 = "123456"
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber1)
        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber2)
        product1.addResult(ResultStatus.FAILED, product1SerialNumber3, "Blurred serial")

        product2.addResult(ResultStatus.COMPLETED, product1SerialNumber2)

        invoice1 = Invoice(
            "ABCDEFGHIJ",
            1,
            "01.01.2019",
            "ACME",
            processedByUser = "User1",
            scanCopyUrl = "https://google.com",
            comment = "Urgent!",
            products = listOf(product1, product2)
        )
    }

    private fun createTestInvoice2() {

        val product1 = Product(1, "6ES7322-1BL00-0AA1", "32 DI module v1", 1)
        val product2 = Product(
            3, "6ES7322-1BL00-0AA1", "32 DI module v1", 1,
            serialNumberPattern = "S[A-Z\\d]{10}")
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        product1.addResult(ResultStatus.COMPLETED, product1SerialNumber3)
        product2.addResult(ResultStatus.COMPLETED, product1SerialNumber2)

        invoice2 = Invoice(
            "KLMNOPRSTU",
            2,
            "02.01.2019",
            "ACME 2",
            processedByUser = "User2",
            scanCopyUrl = "https://google.com",
            products = listOf(product1, product2)
        )
    }
}