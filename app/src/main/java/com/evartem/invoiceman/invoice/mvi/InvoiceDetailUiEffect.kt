package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.entity.doc.Product
import com.evartem.domain.gateway.GatewayError

sealed class InvoiceDetailUiEffect {
    data class Error(val gatewayError: GatewayError?) : InvoiceDetailUiEffect()
    data class ProductClick(val product: Product) : InvoiceDetailUiEffect()
    object SuccessMessage : InvoiceDetailUiEffect()
    object NotAcceptedYetMessage : InvoiceDetailUiEffect()
}