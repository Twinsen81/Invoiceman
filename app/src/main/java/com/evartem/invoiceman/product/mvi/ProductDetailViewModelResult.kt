package com.evartem.invoiceman.product.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult

sealed class ProductDetailViewModelResult {
    data class Product(val gatewayResult: InvoiceGatewayResult) : ProductDetailViewModelResult()
    data class RelayEvent(val uiEvent: ProductDetailEvent) : ProductDetailViewModelResult()
}