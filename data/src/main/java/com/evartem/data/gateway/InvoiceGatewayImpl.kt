package com.evartem.data.gateway

import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.repository.InvoiceRepository
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class InvoiceGatewayImpl(val invoiceRepository: InvoiceRepository, val invoiceMapper: InvoiceMapperToGatewayResult) :
    InvoiceGateway {

    override fun getInvoicesForUser(user: User, refresh: Boolean): Observable<InvoiceGatewayResult> =
        invoiceRepository.getInvoicesForUser(user.id, refresh)
            .map { invoiceMapper.toGateway(it) }

    override fun getInvoice(invoiceId: String): Observable<InvoiceGatewayResult> =
        invoiceRepository.getInvoice(invoiceId)
            .map { invoiceMapper.toGateway(it) }

    override fun requestInvoiceForProcessing(user: User, invoice: Invoice): Observable<InvoiceGatewayResult> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Observable<InvoiceGatewayResult> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}