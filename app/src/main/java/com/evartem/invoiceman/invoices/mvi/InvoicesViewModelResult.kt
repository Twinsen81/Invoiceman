package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class InvoicesViewModelResult(val gatewayResult: InvoiceGatewayResult) {
    class Search(val searchQuery: String, gatewayResult: InvoiceGatewayResult) :
        InvoicesViewModelResult(gatewayResult)

    class Invoices(gatewayResult: InvoiceGatewayResult) : InvoicesViewModelResult(gatewayResult)
    class IsRefreshing : InvoicesViewModelResult(InvoiceGatewayResult.EmptyResult())
}