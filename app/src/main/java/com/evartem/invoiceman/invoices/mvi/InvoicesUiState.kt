package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.doc.Invoice

data class InvoicesUiState(
    var searchRequest: String = "",
    var searchViewOpen: Boolean = false,
    var isLoading: Boolean = false,
    var isRefreshing: Boolean = false,
    var setFocusToSearchView: Boolean = false,
    var sorting: InvoicesEvent.Sort.SortBy = InvoicesEvent.Sort.SortBy.NONE,
    var invoices: MutableList<Invoice> = mutableListOf()
) {
    override fun toString(): String {
        return StringBuilder()
            .append("searchRequest=").append(searchRequest)
            .append(" searchViewOpen=").append(searchViewOpen)
            .append(" isLoading=").append(isLoading)
            .append(" isRefreshing=").append(isRefreshing)
            .append(" sorting=").append(sorting)
            .append(" setFocusToSearchView=").append(setFocusToSearchView)
            .append(" invoices=${invoices.size}").toString()
    }
}
