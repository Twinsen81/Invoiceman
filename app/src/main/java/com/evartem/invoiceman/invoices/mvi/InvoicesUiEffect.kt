package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.GatewayError

sealed class InvoicesUiEffect {
    data class RemoteDatasourceError(val gatewayError: GatewayError) : InvoicesUiEffect()
    data class NoNewData(val message: String? = null) : InvoicesUiEffect()
}