package com.evartem.data.util

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.local.model.ResultStatusLocalModel
import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.GatewayError

class TestDataLocalModel {

    val error = GatewayError(
        GatewayError.ErrorCode.UNKNOWN_ERROR,
        "Test error message",
        IllegalArgumentException("Bad JSON data")
    )
    val invoice1 = createTestInvoice1()
    val invoice2 = createTestInvoice2()
    val invoice1And2 = listOf(invoice1, invoice2)
    val invoice1Updated: InvoiceLocalModel
    val invoice1BeingProcessed: InvoiceLocalModel

    val user = User("test@test.com")

    init {
        invoice1Updated = createTestInvoice1()
        invoice1Updated.number = 81
        invoice1Updated.date = "09.04.2019"

        invoice1BeingProcessed = createTestInvoice1()
        invoice1BeingProcessed.processedByUser = user.id
    }

    private fun createTestInvoice1(): InvoiceLocalModel {

        val product1 = ProductLocalModel(1, "6ES7322-1BL00-0AA0", "32 DI module", 3)
        val product2 =
            ProductLocalModel(2, "6ES7322-1BL00-0AA0", "32 DI module", 2, serialNumberPattern = "S[A-Z\\d]{10}")
        val product1SerialNumber1 = "123456"
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        // result1 = ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber1)
        product1.insertOrUpdateResult(ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber1))
        product1.insertOrUpdateResult(ResultLocalModel(2, ResultStatusLocalModel(), product1SerialNumber2))
        product1.insertOrUpdateResult(
            ResultLocalModel(
                3,
                ResultStatusLocalModel(1),
                product1SerialNumber3,
                "Blurred serial"
            )
        )
        product2.insertOrUpdateResult(ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber2))

        val invoice = InvoiceLocalModel(
            "ABCDEFGHIJ",
            1,
            "01.01.2019",
            "ACME",
            processedByUser = "User1",
            scanCopyUrl = "https://google.com",
            comment = "Urgent!"
        )
        invoice.products.add(product1)
        invoice.products.add(product2)
        return invoice
    }

    private fun createTestInvoice2(): InvoiceLocalModel {

        val product1 = ProductLocalModel(1, "6ES7322-1BL00-0AA1", "32 DI module v1", 1)
        val product2 =
            ProductLocalModel(3, "6ES7322-1BL00-0AA1", "32 DI module v1", 1, serialNumberPattern = "S[A-Z\\d]{10}")
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        product1.insertOrUpdateResult(ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber3))
        product2.insertOrUpdateResult(ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber2))

        val invoice = InvoiceLocalModel(
            "KLMNOPRSTU",
            2,
            "02.01.2019",
            "ACME 2",
            processedByUser = "User2",
            scanCopyUrl = "https://google.com"
        )
        invoice.products.add(product1)
        invoice.products.add(product2)
        return invoice
    }
}