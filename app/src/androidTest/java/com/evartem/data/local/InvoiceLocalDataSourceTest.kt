package com.evartem.data.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.local.model.ResultStatusLocalModel
import com.evartem.invoiceman.TheApp
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InvoiceLocalDataSourceTest {

    @Rule
    @JvmField
    val exception: ExpectedException = ExpectedException.none()

    private lateinit var realm: Realm
    private lateinit var localDataSource: InvoiceLocalDataSource

    private lateinit var invoice1: InvoiceLocalModel
    private lateinit var invoice2: InvoiceLocalModel
    private lateinit var result1: ResultLocalModel

    @Before
    fun setup() {
        localDataSource = InvoiceLocalDataSource()
        initRealm()
        createTestInvoice1()
        createTestInvoice2()
    }

    private fun initRealm() {
        Realm.init(TheApp.get())//InstrumentationRegistry.getInstrumentation().context)
        val realmConfiguration = RealmConfiguration.Builder()
            .inMemory()
            .name("test-realm")
            .schemaVersion(0)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        realm = Realm.getDefaultInstance()
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun insertedInvoiceShouldBeSuccessfullyRetrieved() {
        // GIVEN an invoice
        // WHEN insert it into the local data source
        localDataSource.insertOrUpdateInvoice(invoice1)

        // SHOULD be able to retrieve it with the same data
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0] == invoice1
            }
    }

    @Test
    fun insertedListOfInvoicesShouldBeSuccessfullyRetrieved() {
        // GIVEN a list of invoices
        val twoInvoices = listOf(invoice1, invoice2)

        // WHEN insert that list into the local data source
        localDataSource.insertOrUpdateInvoice(twoInvoices)

        // SHOULD be able to retrieve it with the same data
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice == twoInvoices
            }
    }

    @Test
    fun updatingInvoiceShouldBeSuccessful() {
        // GIVEN an invoice in the data source
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN update its data and insert into the data source again
        addProduct3ToInvoice(invoice1)
        localDataSource.insertOrUpdateInvoice(invoice1)

        // SHOULD be able to retrieve it with the updated data
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0] == invoice1
            }
    }

    @Test
    fun updatingListOfInvoicesShouldBeSuccessful() {
        // GIVEN a list of invoices in the data source
        val twoInvoices = listOf(invoice1, invoice2)
        localDataSource.insertOrUpdateInvoice(twoInvoices)

        // WHEN update its data and insert into the data source again
        addProduct3ToInvoice(twoInvoices[0])
        addProduct3ToInvoice(twoInvoices[1])
        localDataSource.insertOrUpdateInvoice(twoInvoices)

        // SHOULD be able to retrieve it with the updated data
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice == twoInvoices
            }
    }

    @Test
    fun insertedResultShouldBeSuccessfullyRetrieved() {
        // GIVEN an invoice in the data source, and a new result for that invoice
        localDataSource.insertOrUpdateInvoice(invoice1)
        val newResult = ResultLocalModel(2, ResultStatusLocalModel(), "S0123456789")

        // WHEN insert the result into the invoice
        localDataSource.insertOrUpdateResult(invoice1.id, 2, newResult)

        // SHOULD be able to retrieve the invoice with the new result
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0].products
                    .first { it.id == 2 }
                    .results.any { result -> result.serial == newResult.serial }
            }
    }

    @Test
    fun insertingResultWithUnknownInvoiceShouldFail() {
        // GIVEN an invoice in the data source, and a new result for that invoice
        localDataSource.insertOrUpdateInvoice(invoice1)
        val newResult = ResultLocalModel(2, ResultStatusLocalModel(), "S0123456789")

        // WHEN insert the result into a non-existent invoice
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.insertOrUpdateResult("no-such-id", 1, newResult)
    }

    @Test
    fun insertingResultWithUnknownProductShouldFail() {
        // GIVEN an invoice in the data source, and a new result for that invoice
        localDataSource.insertOrUpdateInvoice(invoice1)
        val newResult = ResultLocalModel(2, ResultStatusLocalModel(), "S0123456789")

        // WHEN insert the result into a non-existent product
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.insertOrUpdateResult(invoice1.id, 9999, newResult)
    }

    @Test
    fun updatingResultShouldBeSuccessful() {
        // GIVEN an invoice in the data source with a result
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN update the result's data and insert into the data source again
        val updatedResult = ResultLocalModel(1, ResultStatusLocalModel(1), null, "Not found")
        localDataSource.insertOrUpdateResult(invoice1.id, 1, updatedResult)

        // SHOULD be able to retrieve the invoice with the updated result
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0].products
                    .first { product -> product.id == 1 }
                    .results.first { result -> result.id == 1 } == updatedResult
            }
    }

    @Test
    fun deletingResultShouldBeSuccessful() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN deleting a result form the invoice
        localDataSource.deleteResult(invoice1.id, 1, result1)

        // SHOULD retrieve the invoice without the deleted result
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0].products
                    .first { product -> product.id == 1 }
                    .results.none { result -> result.id == result1.id }
            }
    }

    @Test
    fun deletingResultWithUnknownInvoiceShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN delete a result from a non-existent invoice
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteResult("no-such-id", 1, result1)
    }

    @Test
    fun deletingResultWithUnknownProductShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN delete a result from a non-existent product
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteResult(invoice1.id, 9999, result1)
    }

    @Test
    fun deletingAllResultsShouldBeSuccessful() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN delete all results from the invoice for a product
        localDataSource.deleteAllResults(invoice1.id, 1)

        // SHOULD retrieve the invoice with the product containing no results
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0].products
                    .first { product -> product.id == 1 }
                    .results.isEmpty()
            }
    }

    @Test
    fun deletingAllResultsWithUnknownInvoiceShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN delete all results from a non-existent invoice
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteAllResults("no-such-id", 1)
    }

    @Test
    fun deletingAllResultsWithUnknownProductShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(invoice1)

        // WHEN delete all results from a non-existent product
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteAllResults(invoice1.id, 9999)
    }

    //region HELPER METHODS
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

        product1.insertOrUpdateResult(ResultLocalModel(10, ResultStatusLocalModel(), product1SerialNumber3))
        product2.insertOrUpdateResult(ResultLocalModel(11, ResultStatusLocalModel(), product1SerialNumber2))

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

    private fun addProduct3ToInvoice(invoice: InvoiceLocalModel) {
        val product3 = ProductLocalModel(3, "6ES7321-1BL00-0AA0", "32 DO module", 4)
        product3.insertOrUpdateResult(
            ResultLocalModel(
                1,
                ResultStatusLocalModel(1),
                "1256...",
                "Damaged packaging"
            )
        )
        invoice.products.add(product3)
    }
    //endregion
}