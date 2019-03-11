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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InvoiceLocalDataSourceTest {

    private lateinit var realm: Realm
    private lateinit var localDataSource: InvoiceLocalDataSource

    private lateinit var invoice1: InvoiceLocalModel
    private lateinit var invoice2: InvoiceLocalModel

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
            .assertValue {retrievedInvoice: List<InvoiceLocalModel> ->
                retrievedInvoice[0] == invoice1}

    }

    @Test
    fun insertedListOfInvoicesShouldBeSuccessfullyRetrieved() {
        // GIVEN a list of invoices
        // WHEN insert that list into the local data source
        // SHOULD be able to retrieve it with the same data
        assertTrue(false)
    }

    @Test
    fun updatingInvoiceShouldBeSuccessful() {
        // GIVEN an invoice in the data source
        // WHEN update its' data and insert into the data source again
        // SHOULD be able to retrieve it with the updated data
        assertTrue(false)
    }

    @Test
    fun updatingListOfInvoicesShouldBeSuccessful() {
        // GIVEN a list of invoices in the data source
        // WHEN update it's data and insert into the data source again
        // SHOULD be able to retrieve it with the updated data
        assertTrue(false)
    }

    @Test
    fun insertedResultShouldBeSuccessfullyRetrieved() {
        // GIVEN an invoice in the data source, and a new result for that invoice
        // WHEN insert the result into the invoice
        // SHOULD be able to retrieve the invoice with the new result
        assertTrue(false)
    }


    @Test
    fun insertingResultWithUnknownInvoiceShouldFail() {assertTrue(false)}

    @Test
    fun insertingResultWithUnknownProductShouldFail() {assertTrue(false)}

    @Test
    fun updatingResultShouldBeSuccessful() {
        // GIVEN an invoice in the data source with a result
        // WHEN update the result's data and insert into the data source again
        // SHOULD be able to retrieve the invoice with the updated result
        assertTrue(false)
    }

    @Test
    fun deletingResultShouldBeSuccessful() {assertTrue(false)}

    @Test
    fun deletingResultWithUnknownInvoiceShouldFail() {assertTrue(false)}

    @Test
    fun deletingResultWithUnknownProductShouldFail() {assertTrue(false)}

    @Test
    fun deletingAllResultsShouldBeSuccessful() {assertTrue(false)}

    @Test
    fun deletingAllResultsWithUnknownInvoiceShouldFail() {assertTrue(false)}

    @Test
    fun deletingAllResultsWithUnknownProductShouldFail() {assertTrue(false)}

    private fun createTestInvoice1() {

        val product1 = ProductLocalModel(1, "6ES7322-1BL00-0AA0", "32 DI module", 3)
        val product2 =
            ProductLocalModel(2, "6ES7322-1BL00-0AA0", "32 DI module", 1, serialNumberPattern = "S[A-Z\\d]{10}")
        val product1SerialNumber1 = "123456"
        val product1SerialNumber2 = "SABC4567890"
        val product1SerialNumber3 = "345678"

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
}