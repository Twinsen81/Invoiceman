package com.evartem.invoiceman.invoices.mvi

sealed class InvoicesEvent {
    object Empty : InvoicesEvent()
    object LoadScreen : InvoicesEvent()
    object RefreshScreen : InvoicesEvent()
    data class Search(val searchQuery: String = "", val startSearch: Boolean = false, val stopSearch: Boolean = false) : InvoicesEvent()
    data class Sort(val sortBy: SortBy) : InvoicesEvent() {
        enum class SortBy {
            NONE,
            NUMBER,
            DATE,
            SELLER
        }
    }
}

typealias SortInvoicesBy = InvoicesEvent.Sort.SortBy