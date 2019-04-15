package com.evartem.domain.interactor

import com.evartem.domain.entity.doc.Result
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * Insert new or update an existing result for the product.
 */
class InsertOrUpdateResultUseCase(schedulers: Schedulers, private val gateway: InvoiceGateway) :
    UseCase<Triple<String, Int, Result>, InvoiceGatewayResult>(schedulers) {

    /**
     * Creates the Observable by forwarding the request to the gateway.
     *
     * @param param invoiceId, productId, result - the invoice, the product and the result itself to insert or update.
     */
    override fun buildObservable(param: Triple<String, Int, Result>?): Observable<InvoiceGatewayResult> {
        requireNotNull(param)
        return gateway.insertOrUpdateResult(invoiceId = param.first, productId = param.second, result = param.third)
    }
}