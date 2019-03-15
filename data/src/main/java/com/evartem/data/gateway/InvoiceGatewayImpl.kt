package com.evartem.data.gateway

import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.repository.InvoiceRepository
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class InvoiceGatewayImpl(val invoiceRepository: InvoiceRepository,
                         val invoiceMapper: InvoiceMapperToGatewayResult): InvoiceGateway
{
    override fun getInvoicesForUser(userId: User, refresh: Boolean): Observable<InvoiceGatewayResult> =
        invoiceRepository.getInvoicesForUser(userId.id, refresh)
            .onErrorReturn { exception ->
                InvoiceRepositoryResult.InvoicesRequestResult(listOf(), InvoiceGatewayResult.ResponseCode.APP_ERROR,
                    InvoiceGatewayResult.NetworkError(0, exception.message ?: "Unknown error")
                )
            }
            .map { invoiceMapper.toGateway(it) }

    override fun requestInvoiceForProcessing(userId: User, invoice: Invoice): Observable<InvoiceGatewayResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun submitInvoicesWithResults(userId: User, invoices: List<Invoice>): Observable<InvoiceGatewayResult> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}