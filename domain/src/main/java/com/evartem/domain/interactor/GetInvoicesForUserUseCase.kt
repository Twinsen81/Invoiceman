package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

class GetInvoicesForUserUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway):
    UseCase<Pair<User, Boolean>, InvoiceGatewayResult>(schedulers) {

    override fun buildObservable(param: Pair<User, Boolean>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.getInvoicesForUser(user = param.first, refresh = param.second)
    }
}

