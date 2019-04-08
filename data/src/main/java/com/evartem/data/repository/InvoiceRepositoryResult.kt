package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.domain.gateway.GatewayError

sealed class InvoiceRepositoryResult {

    data class Invoices(val invoices: List<InvoiceLocalModel>) : InvoiceRepositoryResult()

    data class Invoice(val invoice: InvoiceLocalModel) : InvoiceRepositoryResult()

    object AcceptConfirmed : InvoiceRepositoryResult()
    object ReturnConfirmed : InvoiceRepositoryResult()

    data class Error(val gatewayError: GatewayError) : InvoiceRepositoryResult()
}