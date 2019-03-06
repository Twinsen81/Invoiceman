package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class GetInvoicesForUserUseCase(val schedulers: Schedulers, val gateway: InvoiceGateway):
    UseCase<User, InvoiceGatewayResult.InvoicesRequestResult>(schedulers) {

    override fun buildObservable(param: User?): Observable<InvoiceGatewayResult.InvoicesRequestResult> {
        requireNotNull(param)
        return gateway.getInvoicesForUser(param).toObservable()
    }
}

