package com.evartem.domain.gateway

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import io.reactivex.Single

interface InvoiceGateway {

    fun getInvoicesForUser(user: User, refresh: Boolean = false): Single<InvoiceGatewayResult>

    fun requestInvoiceForProcessing(user: User, invoice: Invoice): Single<InvoiceGatewayResult>

    fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Single<InvoiceGatewayResult>

}