package com.evartem.invoiceman.invoices

sealed class InvoicesUiEvent {
    object LoadScreenUiEvent : InvoicesUiEvent()
    object RefreshUiEvent : InvoicesUiEvent()
    data class SearchUiEvent(val searchQuery: String) : InvoicesUiEvent()
}