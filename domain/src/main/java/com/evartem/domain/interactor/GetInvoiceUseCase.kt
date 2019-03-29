package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class GetInvoiceUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway):
    UseCase<String, InvoiceGatewayResult>(schedulers) {

    override fun buildObservable(param: String?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.getInvoice(invoiceId = param)
    }
}