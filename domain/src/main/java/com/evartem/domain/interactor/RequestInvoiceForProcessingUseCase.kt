package com.evartem.domain.interactor

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Ask the server's permission to process the invoice by the user. The server may decline the request if another user
 * has already requested that invoice for processing.
 */
class RequestInvoiceForProcessingUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<Pair<User, String>, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param user - the user who wants to process an invoice,
     * invoiceId - the invoice that is requested for processing.
     */
    override fun buildObservable(param: Pair<User, String>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.requestInvoiceForProcessing(user = param.first, invoiceId = param.second)
    }
}