package com.evartem.invoiceman.invoice_detail.mvi

sealed class InvoiceDetailEvent {
    object Empty : InvoiceDetailEvent()
    object LoadScreen : InvoiceDetailEvent()
}
