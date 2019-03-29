package com.evartem.invoiceman.invoice_detail.mvi

import com.evartem.domain.entity.doc.Invoice

data class InvoiceDetailUiState(
    var invoice: Invoice,
    var isLoading: Boolean = false
) {
    override fun toString(): String {
        return StringBuilder()
            .append("invoice_id=").append(invoice.id)
            .append(" isLoading=").append(isLoading)
            .append(" products=${invoice.products.size}").toString()
    }

    companion object {
        val EmptyUiState
            get() = InvoiceDetailUiState(Invoice("", 0, "", "", listOf()))
    }
}
