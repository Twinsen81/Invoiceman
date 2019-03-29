package com.evartem.invoiceman.invoice_detail.mvi

import com.evartem.domain.gateway.GatewayError

sealed class InvoiceDetailUiEffect {
    data class RemoteDatasourceError(val gatewayError: GatewayError?) : InvoiceDetailUiEffect()
}