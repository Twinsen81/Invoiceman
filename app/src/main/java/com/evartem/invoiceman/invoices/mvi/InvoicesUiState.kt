package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.doc.Invoice

data class InvoicesUiState(
    var searchRequest: String = "",
    var searchViewOpen: Boolean = false,
    var isLoading: Boolean = false,
    var isRefreshing: Boolean = false,
    var isInvoicesChanged: Boolean = true,
    var invoices: List<Invoice> = emptyList()
) {
    override fun toString(): String {
        return StringBuilder()
            .append("searchRequest=").append(searchRequest)
            .append(" searchViewOpen=").append(searchViewOpen)
            .append(" isLoading=").append(isLoading)
            .append(" isRefreshing=").append(isRefreshing)
            .append(" isInvoicesChanged=").append(isInvoicesChanged)
            .append(" invoices=${invoices.size}").toString()
    }
}
