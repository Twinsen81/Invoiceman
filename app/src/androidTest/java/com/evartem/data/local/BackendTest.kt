package com.evartem.data.local

import androidx.test.runner.AndroidJUnit4
import com.evartem.backendsim.InvoiceBackendSimulation
import com.evartem.data.remote.api.InvoiceApi
import com.evartem.data.remote.model.InvoiceRemoteModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackendTest {

    @Before
    fun setup() {
        InvoiceBackendSimulation.startServer(2)
    }

    @Test
    fun response() {
        val api = InvoiceApi(InvoiceBackendSimulation.getBaseUrl())
        printResponse(api.getInvoicesForUser("abc").blockingGet())
    }

    fun printResponse(resp: retrofit2.Response<List<InvoiceRemoteModel>>?) {
        if (resp!!.isSuccessful)
            print("!!!" + resp.body().toString())
    }

    @After
    fun tearDown() {
    }
}