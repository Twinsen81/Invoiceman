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


    val JSON_DATA1 = """
[
	{
		"id": "be7aa229-7d5e-4f90-bd7a-e437ecb6f9de",
		"number": 7,
		"date": "12.03.2019",
		"seller": "Siemens AG",
		"scanCopyUrl": "http://somewebsiteurl.biz/scans/be7aa229-7d5e-4f90-bd7a-e437ecb6f9de.jpg",
		"comment": "Unsealed packagings are OK",
		"products": [
			{
				"id": 1,
				"article": "6ES7515-2AM01-0AB0",
				"description": "SIMATIC S7-1500, CPU 1515-2 PN, Central processing unit with work memory 500 KB for Program and 3 MB for data, 1st interface: PROFINET IRT with 2-port switch, 2nd interface: PROFINET RT, 30 ns bit performance, SIMATIC Memory Card required",
				"quantity": 1,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"serialNumberPattern": "S[A-Z\d]{10}"
			},
			{
				"id": 2,
				"article": "6ES7531-7KF00-0AB0",
				"description": "SIMATIC S7-1500, analog input module AI 8xU/I/RTD/TC ST, 16 bit resolution, accuracy 0.3%, 8 channels in groups of 8, 4 channels for RTD measurement, common mode voltage 10 V; Diagnostics, hardware interrupts Incl. infeed element, shield bracket and shield terminal: Front connector (screw terminals or push-in) to be ordered separately",
				"quantity": 2,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"serialNumberPattern": "S[A-Z\d]{10}"
			},
			{
				"id": 3,
				"article": "6ES7521-1BH00-0AB0",
				"description": "SIMATIC S7-1500, digital input module DI 16xDC 24V HF, 16 channels in groups of 16; input delay 0.05..20 ms; Input type 3 (IEC 61131); Diagnostics, hardware interrupts: Front connector (screw terminals or push-in) to be ordered separately",
				"quantity": 4,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,
				"equalSerialNumbersAreOk": false,
				"serialNumberPattern": "S[A-Z\d]{10}"
			},
			{
				"id": 4,
				"article": "3UF7011-1AB00-0",
				"description": "Basic unit SIMOCODE pro V PN, Ethernet/PROFINET IO, PN system redundancy, OPC UA server, Web server, transmission rate 100 Mbps, 2 x bus connection via RJ45, 4I/3O freely parameterizable, Us: 24 V DC, input for thermistor connection Monostable relay outputs, expandable by extension modules",
				"quantity": 2,
				"articleScanRequired": true,
				"hasSerialNumber": true,
				"serialNumberScanRequired": false,
				"equalSerialNumbersAreOk": true
			}
		]
	},
	{
		"id": "666174ef-0963-4f11-87b1-677adb820455",
		"number": 9,
		"date": "16.03.2019",
		"seller": "Phoenix Contact RUS",
		"products": [
			{
				"id": 1,
				"article": "2891001",
				"description": "Ethernet switch, 5 TP RJ45 ports, automatic detection of data transmission speed of 10 or 100 Mbps (RJ45), autocrossing function",
				"quantity": 2,
				"articleScanRequired": false,
				"hasSerialNumber": true,
				"serialNumberScanRequired": true,

    """
}
