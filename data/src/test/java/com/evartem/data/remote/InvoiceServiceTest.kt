package com.evartem.data.remote

import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.remote.api.createInvoiceNetworkClient
import com.evartem.data.util.TestDataJson
import com.evartem.data.util.TestDataRemoteModel
import io.appflate.restmock.JVMFileParser
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.utils.RequestMatchers.pathStartsWith
import org.junit.After
import org.junit.Before
import org.junit.Test

class InvoiceServiceTest {

    private lateinit var testDataRemote: TestDataRemoteModel
    private lateinit var testDataJson: TestDataJson

    private lateinit var invoiceService: InvoiceService

    @Before
    fun setup() {
        testDataRemote = TestDataRemoteModel()
        testDataJson = TestDataJson()

        RESTMockServerStarter.startSync(JVMFileParser())
        // RESTMockServer.enableLogging(RESTMockConsoleLogger())

        invoiceService = createInvoiceNetworkClient(RESTMockServer.getUrl(), false)
    }

    @After
    fun tearDown() {
        RESTMockServer.shutdown()
    }

    @Test
    fun `Should return properly parsed remote models form the server`() {
        // GIVEN a server that returns two invoices
        RESTMockServer.whenGET(pathStartsWith("/invoices"))
            .thenReturnString(200, testDataJson.invoice1And2)

        // WHEN request data from the server
        val testObserver = invoiceService.getInvoicesForUser("test").test()

        // SHOULD successfully return the two invoices as InvoiceRemoteModel
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { response -> response.code() == 200 }
            .assertValue { response ->
                response.body() == listOf(testDataRemote.invoice1, testDataRemote.invoice2)
            }
    }
}