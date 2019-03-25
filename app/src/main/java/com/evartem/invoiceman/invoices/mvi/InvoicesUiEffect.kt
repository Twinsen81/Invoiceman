package com.evartem.invoiceman.invoices.mvi

sealed class InvoicesUiEffect {
    data class NetworkError(val code: Int, val message: String) : InvoicesUiEffect()
}