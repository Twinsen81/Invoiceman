package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoiceDetailViewModelResult {
    data class Invoice(val gatewayResult: InvoiceGatewayResult) : InvoiceDetailViewModelResult()
    data class AcceptRequest(val gatewayResult: InvoiceGatewayResult) : InvoiceDetailViewModelResult()
    data class ReturnRequest(val gatewayResult: InvoiceGatewayResult) : InvoiceDetailViewModelResult()
    data class RelayEvent(val uiEvent: InvoiceDetailEvent) : InvoiceDetailViewModelResult()
}