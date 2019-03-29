package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoiceDetailViewModelResult(val gatewayResult: InvoiceGatewayResult) {
    class Invoice(gatewayResult: InvoiceGatewayResult) : InvoiceDetailViewModelResult(gatewayResult)
    data class RelayEvent(val uiEvent: InvoiceDetailEvent) : InvoiceDetailViewModelResult(InvoiceGatewayResult.EmptyResult())
}