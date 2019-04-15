package com.evartem.data.repository

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.domain.gateway.GatewayError

/**
 * Defines the types of results that gateway receives from the repository.
 * See [com.evartem.domain.gateway.InvoiceGatewayResult] for details.
 */
sealed class InvoiceRepositoryResult {

    data class Invoices(
        val invoices: List<InvoiceLocalModel>,
        val gatewayError: GatewayError? = null
    ) : InvoiceRepositoryResult()

    data class Invoice(val invoice: InvoiceLocalModel) : InvoiceRepositoryResult()
    data class Product(val product: ProductLocalModel) : InvoiceRepositoryResult()

    object AcceptConfirmed : InvoiceRepositoryResult()
    object ReturnConfirmed : InvoiceRepositoryResult()
    object ResultOperationSucceeded : InvoiceRepositoryResult()

    data class Error(val gatewayError: GatewayError) : InvoiceRepositoryResult()
}