package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.domain.gateway.GatewayError

sealed class InvoiceRepositoryResult(
    val success: Boolean,
    val gatewayError: GatewayError? = null
) {

    class InvoicesRequestResult(
        val invoices: List<InvoiceLocalModel>,
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)

    class ProcessingRequestResult(
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)

    class SubmitInvoiceResult(
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)
}