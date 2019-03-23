package com.evartem.invoiceman.invoices.mvi

sealed class InvoicesEvent {
    object LoadScreenEvent : InvoicesEvent()
    object RefreshScreenEvent : InvoicesEvent()
    data class SearchInvoiceEvent(val searchQuery: String) : InvoicesEvent()
}