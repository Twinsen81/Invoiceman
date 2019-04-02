package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class RequestInvoiceForProcessingUseCase (schedulers: Schedulers, private val gateway: InvoiceGateway):
    UseCase<Pair<User, Invoice>, InvoiceGatewayResult>(schedulers) {

    override fun buildObservable(param: Pair<User, Invoice>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.requestInvoiceForProcessing(user = param.first, invoice = param.second)
    }
}