package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.domain.gateway.GatewayError

sealed class InvoiceRepositoryResult {

    data class Invoices(val invoices: List<InvoiceLocalModel>) : InvoiceRepositoryResult()

    data class Invoice(val invoice: InvoiceLocalModel) : InvoiceRepositoryResult()

    object AcceptConfirmed : InvoiceRepositoryResult()
    object ReturnConfirmed : InvoiceRepositoryResult()

    data class Error(val gatewayError: GatewayError) : InvoiceRepositoryResult()

/*
    class SubmitInvoiceResult(
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)*/
}

/*sealed class InvoiceRepositoryResult(
    val success: Boolean,
    val gatewayError: GatewayError? = null
) {

    class Invoices(
        val invoices: List<InvoiceLocalModel>,
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)

    class Invoice(
        val invoice: InvoiceLocalModel,
        success: Boolean = true,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)

    class ProcessingRequestResult(
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)
*//*
    class SubmitInvoiceResult(
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult(success, gatewayError)*//*
}*/