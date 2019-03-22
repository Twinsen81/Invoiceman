package com.evartem.backendsim

import com.evartem.domain.entity.doc.Invoice
import io.appflate.restmock.JVMFileParser
import io.appflate.restmock.RESTMockServerStarter

class InvoiceBackendSimulation(val initialNumberOfInvoices: Int = 7) {

    val invoices: MutableList<Invoice> = mutableListOf()

    init {

        RESTMockServerStarter.startSync(JVMFileParser())
    }
}

