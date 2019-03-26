package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.doc.Invoice

data class InvoicesUiState(
    val searchRequest: String = "",
    val loadingIndicator: Boolean = false,
    val invoices: List<Invoice> = emptyList()
)
