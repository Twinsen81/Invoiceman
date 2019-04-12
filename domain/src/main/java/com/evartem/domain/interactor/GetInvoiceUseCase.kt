package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Get the invoice with the specified id.
 */
class GetInvoiceUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<String, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param invoiceID - the ID of the invoice to get from the gateway.
     */
    override fun buildObservable(param: String?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.getInvoice(invoiceId = param)
    }
}