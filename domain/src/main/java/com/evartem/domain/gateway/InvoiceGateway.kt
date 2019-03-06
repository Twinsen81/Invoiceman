package com.evartem.domain.gateway

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import io.reactivex.Single

interface InvoiceGateway {

    fun getInvoicesForUser(user: User): Single<InvoiceGatewayResult.InvoicesRequestResult>

    fun requestInvoiceForProcessing(user: User, invoice: Invoice): Single<InvoiceGatewayResult.ProcessingRequestResult>

    fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Single<InvoiceGatewayResult.SubmitInvoiceResult>

}