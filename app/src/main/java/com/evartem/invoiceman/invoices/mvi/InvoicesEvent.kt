package com.evartem.invoiceman.invoices.mvi

sealed class InvoicesEvent {
    object LoadScreen : InvoicesEvent()
    object RefreshScreen : InvoicesEvent()
    data class Search(val searchQuery: String = "", val startSearch: Boolean = false, val stopSearch: Boolean = false) : InvoicesEvent()
}