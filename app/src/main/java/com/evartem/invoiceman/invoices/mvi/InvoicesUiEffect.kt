package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.NetworkError

sealed class InvoicesUiEffect {
    data class RemoteDatasourceError(val networkError: NetworkError) : InvoicesUiEffect()
    data class NoNewData(val message: String? = null) : InvoicesUiEffect()
}