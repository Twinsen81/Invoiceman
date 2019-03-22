package com.evartem.invoiceman.invoices

sealed class InvoicesEvent {
    object LoadScreenEvent : InvoicesEvent()
    object RefreshScreenEvent : InvoicesEvent()
    data class SearchInvoiceEvent(val searchQuery: String) : InvoicesEvent()
}