package com.evartem.domain.gateway

import com.evartem.domain.entity.doc.Invoice

sealed class InvoiceGatewayResult {

    data class InvoicesRequestResult(val response: ResponseCode, val invoices: List<Invoice>) : InvoiceGatewayResult()

    data class ProcessingRequestResult(val response: ResponseCode) : InvoiceGatewayResult()

    data class SubmitInvoiceResult(val response: ResponseCode) : InvoiceGatewayResult()

    enum class ResponseCode {
        SUCCESS,
        DENIED_PERMISSIONS,
        DENIED_TAKEN,
        DENIED_NOT_FOUND,
        DENIED_INCONSISTENT_DATA
    }

    data class NetworkError(val code: Int, val message: String) : InvoiceGatewayResult()
}