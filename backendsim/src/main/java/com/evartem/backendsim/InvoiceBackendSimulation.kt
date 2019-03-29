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
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object InvoiceBackendSimulation {

    private var initialNumberOfInvoices: Int = 5
    private val invoices: MutableMap<String, MutableList<Invoice>> = mutableMapOf()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listOfInvoices: Type = Types.newParameterizedType(List::class.java, Invoice::class.java)
    private val jsonAdapter: JsonAdapter<List<Invoice>> = moshi.adapter<List<Invoice>>(listOfInvoices)

    var responseCode = 200

    var responseDelaySeconds = 2L

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
                getMockResponseInvoicesForUser(request)
            })
            .delay(TimeUnit.SECONDS, responseDelaySeconds)
    }

    private fun getMockResponseInvoicesForUser(request: RecordedRequest?): MockResponse? {
        val uri = Uri.parse(request!!.requestUrl.toString())
        val userId = uri.getQueryParameter("userid")

        if (userId != null && userId.isNotEmpty()) {
            return MockResponse()
                .setBody(jsonAdapter.toJson(getInvoicesForUser(userId).toList())).setResponseCode(responseCode)
        }

        return MockResponse().setBody("incorrect [userid] parameter").setResponseCode(401)
    }

    private fun getInvoicesForUser(userId: String): MutableList<Invoice> {
        if (!invoices.containsKey(userId))
            invoices[userId] = RandomInvoiceGenerator.getInvoices(initialNumberOfInvoices).toMutableList()
        return invoices[userId]!!
    }
}
