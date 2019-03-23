package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoicesViewModelResult(val gatewayResult: InvoiceGatewayResult) {
    class SearchResult(val searchQuery: String, gatewayResult: InvoiceGatewayResult): InvoicesViewModelResult(gatewayResult)
    class AllInvoicesResult(gatewayResult: InvoiceGatewayResult): InvoicesViewModelResult(gatewayResult)
}