package com.evartem.domain.gateway

import com.evartem.domain.entity.doc.Invoice

sealed class InvoiceGatewayResult(val success: Boolean, val networkError: NetworkError? = null) {

    class InvoicesRequestResult(
        val invoices: List<Invoice>,
        success: Boolean,
        networkError: NetworkError? = null
    ) : InvoiceGatewayResult(success, networkError)

    class ProcessingRequestResult(success: Boolean, networkError: NetworkError? = null) :
        InvoiceGatewayResult(success, networkError)

    class SubmitInvoiceResult(success: Boolean, networkError: NetworkError? = null) :
        InvoiceGatewayResult(success, networkError)

    class EmptyResult : InvoiceGatewayResult(true)
}

data class NetworkError(val code: Int, val message: String) {
    companion object {
        fun fromException(exception: Throwable) =
            NetworkError(1000, exception.message ?: "Unknown network error")
    }
}