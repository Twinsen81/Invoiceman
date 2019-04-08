package com.evartem.data.util

import com.evartem.data.remote.model.InvoiceRemoteModel
import com.evartem.data.remote.model.ProductRemoteModel
import com.evartem.data.remote.model.ResultRemoteModel

class TestDataRemoteModel {

    lateinit var invoice1: InvoiceRemoteModel
    lateinit var invoice2: InvoiceRemoteModel

    init {
        createTestInvoice1()
        createTestInvoice2()
    }

    private fun createTestInvoice1() {

        val product1SerialNumber1 = "123456"
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        invoice1 = InvoiceRemoteModel(
            "ABCDEFGHIJ",
            1,
            "01.01.2019",
            "ACME",
            processedByUser = "User1",
            scanCopyUrl = "https://google.com",
            comment = "Urgent!",
            products = listOf(
                ProductRemoteModel(
                    1, "6ES7322-1BL00-0AA0", "32 DI module", 3,
                    results = listOf(
                        ResultRemoteModel(0, product1SerialNumber1, null, 1),
                        ResultRemoteModel(0, product1SerialNumber2, null, 2),
                        ResultRemoteModel(1, product1SerialNumber3, "Blurred serial", 3)
                    )
                ),
                ProductRemoteModel(
                    2, "6ES7322-1BL00-0AA0", "32 DI module", 2, serialNumberPattern = "S[A-Z\\d]{10}",
                    results = listOf(
                        ResultRemoteModel(0, product1SerialNumber2, null, 1)
                    )
                )
            )
        )
    }

    private fun createTestInvoice2() {

        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        invoice2 = InvoiceRemoteModel(
            "KLMNOPRSTU",
            2,
            "02.01.2019",
            "ACME 2",
            processedByUser = "User2",
            scanCopyUrl = "https://google.com",
            comment = "Urgent!",
            products = listOf(
                ProductRemoteModel(
                    1, "6ES7322-1BL00-0AA1", "32 DI module v1", 1,
                    results = listOf(
                        ResultRemoteModel(0, product1SerialNumber3, null, 1)
                    )
                ),
                ProductRemoteModel(
                    3, "6ES7322-1BL00-0AA1", "32 DI module v1", 1, serialNumberPattern = "S[A-Z\\d]{10}",
                    results = listOf(
                        ResultRemoteModel(0, product1SerialNumber2, null, 1)
                    )
                )
            )
        )

    }
}