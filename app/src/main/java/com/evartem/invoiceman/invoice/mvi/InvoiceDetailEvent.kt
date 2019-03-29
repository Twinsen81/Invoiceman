package com.evartem.invoiceman.invoice.mvi

sealed class InvoiceDetailEvent {
    object Empty : InvoiceDetailEvent()
    object LoadScreen : InvoiceDetailEvent()
}
