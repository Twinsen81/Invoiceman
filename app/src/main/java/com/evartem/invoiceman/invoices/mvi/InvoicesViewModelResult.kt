package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoicesViewModelResult {
    data class Invoices(val gatewayResult: InvoiceGatewayResult) : InvoicesViewModelResult()
    data class RelayEvent(val uiEvent: InvoicesEvent) : InvoicesViewModelResult()
}