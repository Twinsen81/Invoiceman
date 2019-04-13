package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Ask the server's permission to return the invoice without completing the processing, so other users could
 * accept it and finish the processing.
 */
class RequestInvoiceReturnUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<Pair<User, String>, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param user - the user who is returning the invoice,
     * invoiceId - the invoice that is being returned.
     */
    override fun buildObservable(param: Pair<User, String>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.requestInvoiceReturn(user = param.first, invoiceId = param.second)
    }
}