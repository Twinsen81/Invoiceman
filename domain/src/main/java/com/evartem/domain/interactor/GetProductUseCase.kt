package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Get the invoice with the specified id.
 */
class GetProductUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<Pair<String, Int>, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param invoiceId, productId - the ID of the product from the invoiceId to get from the gateway.
     */
    override fun buildObservable(param: Pair<String, Int>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.getProduct(invoiceId = param.first, productId = param.second)
    }
}