package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.entity.doc.Invoice

data class InvoiceDetailUiState(
    var invoice: Invoice,
    var displayProgress: Boolean = false,
    var progress: Pair<Int, Int> = 0 to 0,
    var isLoading: Boolean = false,
    var searchRequest: String = "",
    var searchViewOpen: Boolean = false,
    var setFocusToSearchView: Boolean = false
) {
    override fun toString(): String {
        return StringBuilder()
            .append("invoice_id=").append(invoice.id)
            .append(" searchRequest=").append(searchRequest)
            .append(" searchViewOpen=").append(searchViewOpen)
            .append(" setFocusToSearchView=").append(setFocusToSearchView)
            .append(" isLoading=").append(isLoading)
            .append(" products=${invoice.products.size}").toString()
    }

    companion object {
        val EmptyUiState
            get() = InvoiceDetailUiState(Invoice("", 0, "", "", listOf()))
    }
}
