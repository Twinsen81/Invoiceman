package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.gateway.GatewayError

sealed class InvoiceDetailUiEffect {
    data class Error(val gatewayError: GatewayError?) : InvoiceDetailUiEffect()
    data class ProductClick(val productId: Int) : InvoiceDetailUiEffect()
    object SuccessMessage : InvoiceDetailUiEffect()
}