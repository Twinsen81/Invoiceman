package com.evartem.invoiceman.invoices

sealed class InvoicesUiEffect {
    data class NetworkError(val code: Int, val message: String) : InvoicesUiEffect()
}