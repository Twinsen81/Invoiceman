package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.doc.Invoice

data class InvoicesUiState(
    val searchRequest: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val invoices: List<Invoice> = emptyList()
) {
    override fun toString(): String {
        return super.toString()
            .substringBefore("invoices=[")
            .plus("invoices=${invoices.size}")
    }
}
