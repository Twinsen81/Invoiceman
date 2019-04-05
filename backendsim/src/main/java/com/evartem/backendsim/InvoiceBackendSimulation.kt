package com.evartem.backendsim

import android.net.Uri
import com.evartem.domain.entity.doc.Invoice
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.appflate.restmock.JVMFileParser
import io.appflate.restmock.MockAnswer
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.RESTMockServerStarter
import io.appflate.restmock.android.AndroidLogger
import io.appflate.restmock.utils.RequestMatchers.pathStartsWith
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.lang.reflect.Type
import java.net.URLDecoder
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object InvoiceBackendSimulation {

    private var initialNumberOfInvoices: Int = 5
    private val invoices: MutableMap<String, MutableList<Invoice>> = mutableMapOf()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listOfInvoices: Type = Types.newParameterizedType(List::class.java, Invoice::class.java)
    private val jsonAdapter: JsonAdapter<List<Invoice>> = moshi.adapter<List<Invoice>>(listOfInvoices)

    var responseCode = 200
    var addRandomInvoiceOnEachRequest = false

    private var responseDelaySeconds = 2L

    fun startServer(initialNumberOfInvoices: Int = 5, responseDelaySeconds: Long = 2) {
        this.initialNumberOfInvoices = initialNumberOfInvoices
        this.responseDelaySeconds = responseDelaySeconds
        RESTMockServerStarter.startSync(JVMFileParser(), AndroidLogger())
        configureResponses()
    }

    fun getBaseUrl(): String = RESTMockServer.getUrl()

    private fun configureResponses() {
        RESTMockServer.whenGET(pathStartsWith("/invoices?userid="))
            .thenAnswer(MockAnswer { request ->
                getMockResponseInvoicesForUser(request!!)
            })
            .delay(TimeUnit.SECONDS, responseDelaySeconds)

        RESTMockServer.whenPUT(pathStartsWith("/requestAccept"))
            .thenAnswer(MockAnswer { request ->
                userRequestsInvoiceProcessing(request!!)
            })
            .delay(TimeUnit.SECONDS, responseDelaySeconds)

        RESTMockServer.whenPUT(pathStartsWith("/requestReturn"))
            .thenAnswer(MockAnswer { request ->
                userRequestsInvoiceReturn(request!!)
            })
            .delay(TimeUnit.SECONDS, responseDelaySeconds)

    }

    private fun userRequestsInvoiceProcessing(request: RecordedRequest): MockResponse? {

        val fields = getFieldsFromBody(request.body.readUtf8())
        val userId = URLDecoder.decode(fields.getOrElse("userid") { "" }, "UTF-8")
        val invoiceId = URLDecoder.decode(fields.getOrElse("invoiceid") { "" }, "UTF-8")

        if (userId.isBlank())
            return MockResponse().setBody("You are not authorized to do that").setResponseCode(401)

        if (userId.isNotBlank() && invoiceId.isNotBlank()) {

            val invoice = getInvoicesForUser(userId).find { invoice -> invoice.id == invoiceId }

            return if (invoice == null)
                MockResponse().setBody("Invoice with id=$invoiceId isn't found").setResponseCode(404)
            else {
                invoice.processedByUser = userId
                MockResponse().setBody("Successfully assigned invoice($invoiceId) to user($userId)").setResponseCode(200)
            }
        }

        return MockResponse().setBody("incorrect request parameters").setResponseCode(400)
    }

    private fun userRequestsInvoiceReturn(request: RecordedRequest): MockResponse? {

        val fields = getFieldsFromBody(request.body.readUtf8())
        val userId = URLDecoder.decode(fields.getOrElse("userid") { "" }, "UTF-8")
        val invoiceId = URLDecoder.decode(fields.getOrElse("invoiceid") { "" }, "UTF-8")

        if (userId.isBlank())
            return MockResponse().setBody("You are not authorized to do that").setResponseCode(401)

        if (userId.isNotBlank() && invoiceId.isNotBlank()) {

            val invoice = getInvoicesForUser(userId).find { invoice -> invoice.id == invoiceId }

            return if (invoice == null)
                MockResponse().setBody("Invoice with id=$invoiceId isn't found").setResponseCode(404)
            else {
                invoice.processedByUser = ""
                MockResponse().setBody("Successfully returned invoice($invoiceId) from user($userId)").setResponseCode(200)
            }
        }

        return MockResponse().setBody("incorrect request parameters").setResponseCode(400)
    }

    private fun getFieldsFromBody(body: String) =
        body.split("&")
            .map { nameValue -> getNameValuePair(nameValue) }
            .toMap()

    private fun getNameValuePair(nameValue: String): Pair<String, String> {
        val strings = nameValue.split("=")
        val name = strings[0]
        val value = if (strings.size > 1) strings[1] else ""
        return Pair(name, value)
    }

    private fun getMockResponseInvoicesForUser(request: RecordedRequest): MockResponse? {
        val uri = Uri.parse(request.requestUrl.toString())
        val userId = uri.getQueryParameter("userid")

        if (userId != null && userId.isNotBlank()) {
            return MockResponse()
                .setBody(jsonAdapter.toJson(getInvoicesForUser(userId).toList())).setResponseCode(responseCode)
        }

        return MockResponse().setBody("incorrect [userid] parameter").setResponseCode(400)
    }

    private fun getInvoicesForUser(userId: String): MutableList<Invoice> {
        if (!invoices.containsKey(userId))
            invoices[userId] = RandomInvoiceGenerator.getInvoices(initialNumberOfInvoices).toMutableList()
        val invoicesForUser: MutableList<Invoice> = invoices[userId]!!

        // Randomly add an invoice (and remove one if the size of the list gets too big)
        if (addRandomInvoiceOnEachRequest && Random.nextBoolean()) {
            invoicesForUser.add(RandomInvoiceGenerator.getInvoices(1).toMutableList()[0])
            if (invoicesForUser.size > initialNumberOfInvoices + 1)
                invoicesForUser.removeAt((0 until (invoicesForUser.size - 1)).random())
        }

        return invoicesForUser
    }
}
