package com.evartem.domain.gateway

sealed class InvoiceGatewayResult {

    data class Invoices(val invoices: List<com.evartem.domain.entity.doc.Invoice>) : InvoiceGatewayResult()

    data class Invoice(val invoice: com.evartem.domain.entity.doc.Invoice) : InvoiceGatewayResult()

    object AcceptConfirmed : InvoiceGatewayResult()
    object ReturnConfirmed : InvoiceGatewayResult()

    data class Error(val gatewayError: GatewayError) : InvoiceGatewayResult()
}

/*sealed class InvoiceGatewayResult(val success: Boolean, val gatewayError: GatewayError? = null) {

    class Invoices(
        val invoices: List<Invoice>,
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceGatewayResult(success, gatewayError)

    class Invoice(
        val invoice: Invoice,
        success: Boolean,
        gatewayError: GatewayError? = null
    ) : InvoiceGatewayResult(success, gatewayError)

    class ProcessingRequestResult(success: Boolean, gatewayError: GatewayError? = null) :
        InvoiceGatewayResult(success, gatewayError)

    class SubmitInvoiceResult(success: Boolean, gatewayError: GatewayError? = null) :
        InvoiceGatewayResult(success, gatewayError)

    class EmptyResult : InvoiceGatewayResult(true)
}*/
