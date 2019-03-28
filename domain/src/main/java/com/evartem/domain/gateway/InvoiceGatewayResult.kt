package com.evartem.domain.gateway

import com.evartem.domain.entity.doc.Invoice

sealed class InvoiceGatewayResult(val success: Boolean, val gatewayError: GatewayError? = null) {

    class InvoicesRequestResult(
        val invoices: List<Invoice>,
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceGatewayResult(success, gatewayError)

    class ProcessingRequestResult(success: Boolean, gatewayError: GatewayError? = null) :
        InvoiceGatewayResult(success, gatewayError)

    class SubmitInvoiceResult(success: Boolean, gatewayError: GatewayError? = null) :
        InvoiceGatewayResult(success, gatewayError)

    class EmptyResult : InvoiceGatewayResult(true)
}
