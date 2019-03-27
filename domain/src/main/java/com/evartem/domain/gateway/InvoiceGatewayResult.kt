package com.evartem.domain.gateway

import com.evartem.domain.entity.doc.Invoice

sealed class InvoiceGatewayResult(val response: ResponseCode, val networkError: NetworkError? = null) {

    class InvoicesRequestResult(
        val invoices: List<Invoice>,
        response: ResponseCode,
        networkError: NetworkError? = null
    ) : InvoiceGatewayResult(response, networkError)

    class ProcessingRequestResult(response: ResponseCode, networkError: NetworkError? = null) :
        InvoiceGatewayResult(response, networkError)

    class SubmitInvoiceResult(response: ResponseCode, networkError: NetworkError? = null) :
        InvoiceGatewayResult(response, networkError)

    class EmptyResult : InvoiceGatewayResult(ResponseCode.SUCCESS)

    enum class ResponseCode {
        SUCCESS,
        DENIED_NETWORK_ERROR,
        DENIED_PERMISSIONS,
        DENIED_TAKEN,
        DENIED_NOT_FOUND,
        DENIED_INCONSISTENT_DATA,
        APP_ERROR
    }

    data class NetworkError(val code: Int, val message: String)
}