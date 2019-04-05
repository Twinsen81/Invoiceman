package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class RequestInvoiceReturnUseCase (schedulers: Schedulers, private val gateway: InvoiceGateway):
    UseCase<Pair<User, String>, InvoiceGatewayResult>(schedulers) {

    override fun buildObservable(param: Pair<User, String>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.requestInvoiceReturn(user = param.first, invoiceId = param.second)
    }
}