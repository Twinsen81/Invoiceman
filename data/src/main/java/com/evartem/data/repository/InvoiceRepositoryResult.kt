package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.domain.gateway.NetworkError

sealed class InvoiceRepositoryResult(
    val success: Boolean,
    val networkError: NetworkError? = null
) {

    class InvoicesRequestResult(
        val invoices: List<InvoiceLocalModel>,
        success: Boolean,
        networkError: NetworkError? = null
    ) : InvoiceRepositoryResult(success, networkError)

    class ProcessingRequestResult(
        success: Boolean,
        networkError: NetworkError? = null
    ) : InvoiceRepositoryResult(success, networkError)

    class SubmitInvoiceResult(
        success: Boolean,
        networkError: NetworkError? = null
    ) : InvoiceRepositoryResult(success, networkError)
}