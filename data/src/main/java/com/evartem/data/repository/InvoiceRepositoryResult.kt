package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel

sealed class InvoiceRepositoryResult {

    data class InvoicesRequestResult(val invoices: List<InvoiceLocalModel>, val response: ResponseCode,
                                     val networkError: NetworkError? = null) : InvoiceRepositoryResult()

    data class ProcessingRequestResult(val response: ResponseCode, val networkError: NetworkError? = null) : InvoiceRepositoryResult()

    data class SubmitInvoiceResult(val response: ResponseCode, val networkError: NetworkError? = null) : InvoiceRepositoryResult()

    enum class ResponseCode {
        SUCCESS,
        DENIED_NETWORK_ERROR,
        DENIED_PERMISSIONS,
        DENIED_TAKEN,
        DENIED_NOT_FOUND,
        DENIED_INCONSISTENT_DATA,
    }

    data class NetworkError(val code: Int, val message: String)
}