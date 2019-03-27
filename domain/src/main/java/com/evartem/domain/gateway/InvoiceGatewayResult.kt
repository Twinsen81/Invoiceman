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
        SERVER_ERROR
    }

    companion object {
        fun getResponseCode(httpCode: Int) =
            when (httpCode) {
                200 -> ResponseCode.SUCCESS
                400 -> ResponseCode.DENIED_INCONSISTENT_DATA
                401 -> ResponseCode.DENIED_PERMISSIONS
                404 -> ResponseCode.DENIED_NOT_FOUND
                409 -> ResponseCode.DENIED_TAKEN
                500 -> ResponseCode.SERVER_ERROR
                else -> ResponseCode.DENIED_NETWORK_ERROR
            }
    }
}

data class NetworkError(val code: Int, val message: String)