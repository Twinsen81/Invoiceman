package com.evartem.data.local

import androidx.test.runner.AndroidJUnit4
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.local.model.ResultStatusLocalModel
import com.evartem.data.local.util.TestData
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

    private lateinit var testData: TestData

    @Before
    fun setup() {
        localDataSource = InvoiceLocalDataSource()
        initRealm()
        testData = TestData()
    }

    private fun initRealm() {
        Realm.init(TheApp.get())
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
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // SHOULD be able to retrieve it with the same data
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0] == testData.invoice1
            }
    }

    @Test
    fun insertedListOfInvoicesShouldBeSuccessfullyRetrieved() {
        // GIVEN a list of invoices
        val twoInvoices = listOf(testData.invoice1, testData.invoice2)

        // WHEN insert that list into the local data source
        localDataSource.insertOrUpdateInvoices(twoInvoices)

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
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN update its data and insert into the data source again
        addProduct3ToInvoice(testData.invoice1)
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // SHOULD be able to retrieve it with the updated data
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0] == testData.invoice1
            }
    }

    @Test
    fun updatingListOfInvoicesShouldBeSuccessful() {
        // GIVEN a list of invoices in the data source
        val twoInvoices = listOf(testData.invoice1, testData.invoice2)
        localDataSource.insertOrUpdateInvoices(twoInvoices)

        // WHEN update its data and insert into the data source again
        addProduct3ToInvoice(twoInvoices[0])
        addProduct3ToInvoice(twoInvoices[1])
        localDataSource.insertOrUpdateInvoices(twoInvoices)

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
        localDataSource.insertOrUpdateInvoice(testData.invoice1)
        val newResult = ResultLocalModel(2, ResultStatusLocalModel(), "S0123456789")

        // WHEN insert the result into the invoice
        localDataSource.insertOrUpdateResult(testData.invoice1.id, 2, newResult)

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
        localDataSource.insertOrUpdateInvoice(testData.invoice1)
        val newResult = ResultLocalModel(2, ResultStatusLocalModel(), "S0123456789")

        // WHEN insert the result into a non-existent invoice
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.insertOrUpdateResult("no-such-id", 1, newResult)
    }

    @Test
    fun insertingResultWithUnknownProductShouldFail() {
        // GIVEN an invoice in the data source, and a new result for that invoice
        localDataSource.insertOrUpdateInvoice(testData.invoice1)
        val newResult = ResultLocalModel(2, ResultStatusLocalModel(), "S0123456789")

        // WHEN insert the result into a non-existent product
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.insertOrUpdateResult(testData.invoice1.id, 9999, newResult)
    }

    @Test
    fun updatingResultShouldBeSuccessful() {
        // GIVEN an invoice in the data source with a result
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN update the result's data and insert into the data source again
        val updatedResult = ResultLocalModel(1, ResultStatusLocalModel(1), null, "Not found")
        localDataSource.insertOrUpdateResult(testData.invoice1.id, 1, updatedResult)

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
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN deleting a result form the invoice
        localDataSource.deleteResult(testData.invoice1.id, 1, testData.result1)

        // SHOULD retrieve the invoice without the deleted result
        localDataSource.getInvoices()
            .test()
            .assertNoErrors()
            .assertValue { retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0].products
                    .first { product -> product.id == 1 }
                    .results.none { result -> result.id == testData.result1.id }
            }
    }

    @Test
    fun deletingResultWithUnknownInvoiceShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN delete a result from a non-existent invoice
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteResult("no-such-id", 1, testData.result1)
    }

    @Test
    fun deletingResultWithUnknownProductShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN delete a result from a non-existent product
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteResult(testData.invoice1.id, 9999, testData.result1)
    }

    @Test
    fun deletingAllResultsShouldBeSuccessful() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN delete all results from the invoice for a product
        localDataSource.deleteAllResults(testData.invoice1.id, 1)

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
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN delete all results from a non-existent invoice
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteAllResults("no-such-id", 1)
    }

    @Test
    fun deletingAllResultsWithUnknownProductShouldFail() {
        // GIVEN an invoice in the data source with some results
        localDataSource.insertOrUpdateInvoice(testData.invoice1)

        // WHEN delete all results from a non-existent product
        // SHOULD throw an IllegalArgumentException
        exception.expect(IllegalArgumentException::class.java)
        localDataSource.deleteAllResults(testData.invoice1.id, 9999)
    }

    //region HELPER METHODS
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