package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoicesViewModelResult(val gatewayResult: InvoiceGatewayResult) {
    class Invoices(gatewayResult: InvoiceGatewayResult) : InvoicesViewModelResult(gatewayResult)
    data class RelayEvent(val uiEvent: InvoicesEvent) : InvoicesViewModelResult(InvoiceGatewayResult.EmptyResult())
}