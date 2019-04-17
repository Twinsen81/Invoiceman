package com.evartem.invoiceman.invoice.mvi

sealed class InvoiceDetailEvent {
    object Empty : InvoiceDetailEvent()
    object LoadScreen : InvoiceDetailEvent()
    object Accept : InvoiceDetailEvent()
    object Return : InvoiceDetailEvent()
    object Submit : InvoiceDetailEvent()
    data class Click(val productId: Int) : InvoiceDetailEvent()
    data class Search(val searchQuery: String = "", val startSearch: Boolean = false, val stopSearch: Boolean = false) :
        InvoiceDetailEvent()
}
