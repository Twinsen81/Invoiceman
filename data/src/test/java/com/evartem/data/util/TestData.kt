package com.evartem.data.util

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.local.model.ResultStatusLocalModel
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Product
import com.evartem.domain.entity.doc.ResultStatus

class TestDataLocalModel {

    lateinit var invoice1: InvoiceLocalModel
    lateinit var invoice2: InvoiceLocalModel
    lateinit var result1: ResultLocalModel

    val user = User("test@test.com")

    init {
        createTestInvoice1()
        createTestInvoice2()
    }

    private fun createTestInvoice1() {

        val product1 = ProductLocalModel(1, "6ES7322-1BL00-0AA0", "32 DI module", 3)
        val product2 =
            ProductLocalModel(2, "6ES7322-1BL00-0AA0", "32 DI module", 2, serialNumberPattern = "S[A-Z\\d]{10}")
        val product1SerialNumber1 = "123456"
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        result1 = ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber1)
        product1.insertOrUpdateResult(result1)
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

        invoice1 = InvoiceLocalModel(
            "ABCDEFGHIJ",
            1,
            "01.01.2019",
            "ACME",
            processedByUser = "User1",
            scanCopyUrl = "https://google.com",
            comment = "Urgent!"
        )
        invoice1.products.add(product1)
        invoice1.products.add(product2)
    }

    private fun createTestInvoice2() {

        val product1 = ProductLocalModel(1, "6ES7322-1BL00-0AA1", "32 DI module v1", 1)
        val product2 =
            ProductLocalModel(3, "6ES7322-1BL00-0AA1", "32 DI module v1", 1, serialNumberPattern = "S[A-Z\\d]{10}")
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

        product1.insertOrUpdateResult(ResultLocalModel(1, ResultStatusLocalModel(), product1SerialNumber3))
        product2.insertOrUpdateResult(ResultLocalModel(2, ResultStatusLocalModel(), product1SerialNumber2))

        invoice2 = InvoiceLocalModel(
            "KLMNOPRSTU",
            2,
            "02.01.2019",
            "ACME 2",
            processedByUser = "User2",
            scanCopyUrl = "https://google.com"
        )
        invoice2.products.add(product1)
        invoice2.products.add(product2)
    }

    private fun convertTestInvoice2ToEntity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun convertTestInvoice1ToEntity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

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