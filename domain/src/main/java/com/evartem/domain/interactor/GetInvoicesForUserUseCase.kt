package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Returns invoices that the user can process or is already processing.
 */
class GetInvoicesForUserUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<Pair<User, Boolean>, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param user - the currently logged in user, refresh - request data from the remote data source if true,
     * otherwise - return only locally cached data.
     */
    override fun buildObservable(param: Pair<User, Boolean>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.getInvoicesForUser(user = param.first, refresh = param.second)
    }
}

