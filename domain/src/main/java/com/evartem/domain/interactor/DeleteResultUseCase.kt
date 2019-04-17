package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Delete the result from a product.
 */
class DeleteResultUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<Triple<String, Int, Int>, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param invoiceId, productId, resultId - the ID of the result to delete.
     */
    override fun buildObservable(param: Triple<String, Int, Int>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.deleteResult(invoiceId = param.first, productId = param.second, resultId = param.third)
    }
}