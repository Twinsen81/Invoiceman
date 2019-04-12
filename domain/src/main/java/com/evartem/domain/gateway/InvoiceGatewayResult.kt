package com.evartem.domain.gateway

/**
 * Defines the types of results that use cases receive from the gateway.
 */
sealed class InvoiceGatewayResult {

    /**
     * The result of requesting invoices available for the user.
     *
     * @property invoices invoices that the user is processing or can request for processing.
     * @property gatewayError the information about a failed request to the remote data source (if any)
     */
    data class Invoices(
        val invoices: List<com.evartem.domain.entity.doc.Invoice>,
        val gatewayError: GatewayError? = null
    ) : InvoiceGatewayResult()

    /**
     * The result of requesting an invoice from the local data source.
     */
    data class Invoice(val invoice: com.evartem.domain.entity.doc.Invoice) : InvoiceGatewayResult()

    /**
     * The server confirmed that the user can start processing the invoice.
     */
    object AcceptConfirmed : InvoiceGatewayResult()

    /**
     * The server confirmed that the user is no longer responsible for precessing the invoice.
     */
    object ReturnConfirmed : InvoiceGatewayResult()

    /**
     * The server confirmed successful receipt of the submitted results.
     */
    object SubmitSucceeded : InvoiceGatewayResult()

    /**
     * The description of the error occurred while executing a gateway request.
     */
    data class Error(val gatewayError: GatewayError) : InvoiceGatewayResult()
}