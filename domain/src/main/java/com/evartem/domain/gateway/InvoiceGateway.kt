package com.evartem.domain.gateway

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import io.reactivex.Observable

interface InvoiceGateway {

    fun getInvoicesForUser(user: User, refresh: Boolean = false): Observable<InvoiceGatewayResult>

    fun getInvoice(invoiceId: String): Observable<InvoiceGatewayResult>

    fun requestInvoiceForProcessing(user: User, invoiceId: String): Observable<InvoiceGatewayResult>

    fun requestInvoiceReturn(user: User, invoiceId: String): Observable<InvoiceGatewayResult>

    fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Observable<InvoiceGatewayResult>
}