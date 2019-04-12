package com.evartem.domain.gateway

sealed class InvoiceGatewayResult {

    data class Invoices(
        val invoices: List<com.evartem.domain.entity.doc.Invoice>,
        val gatewayError: GatewayError? = null
    ) : InvoiceGatewayResult()

    data class Invoice(val invoice: com.evartem.domain.entity.doc.Invoice) : InvoiceGatewayResult()

    object AcceptConfirmed : InvoiceGatewayResult()
    object ReturnConfirmed : InvoiceGatewayResult()
    object SubmitSucceeded : InvoiceGatewayResult()

    data class Error(val gatewayError: GatewayError) : InvoiceGatewayResult()
}