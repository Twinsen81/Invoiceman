package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.gateway.GatewayError
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect

sealed class InvoiceDetailUiEffect {
    data class RemoteDatasourceError(val gatewayError: GatewayError?) : InvoiceDetailUiEffect()
    data class ProductClick(val productId: Int) : InvoiceDetailUiEffect()
}