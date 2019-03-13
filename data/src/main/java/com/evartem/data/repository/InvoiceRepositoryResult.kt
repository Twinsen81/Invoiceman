package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoiceRepositoryResult(
    val response: InvoiceGatewayResult.ResponseCode,
    val networkError: InvoiceGatewayResult.NetworkError? = null
) {

    class InvoicesRequestResult(
        val invoices: List<InvoiceLocalModel>, response: InvoiceGatewayResult.ResponseCode,
        networkError: InvoiceGatewayResult.NetworkError? = null
    ) : InvoiceRepositoryResult(response, networkError)

    class ProcessingRequestResult(
        response: InvoiceGatewayResult.ResponseCode,
        networkError: InvoiceGatewayResult.NetworkError? = null
    ) : InvoiceRepositoryResult(response, networkError)

    class SubmitInvoiceResult(
        response: InvoiceGatewayResult.ResponseCode,
        networkError: InvoiceGatewayResult.NetworkError? = null
    ) : InvoiceRepositoryResult(response, networkError)
}