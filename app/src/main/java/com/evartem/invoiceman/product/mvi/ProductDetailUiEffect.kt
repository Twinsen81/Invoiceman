package com.evartem.invoiceman.product.mvi

import com.evartem.domain.entity.doc.Product
import com.evartem.domain.gateway.GatewayError

sealed class ProductDetailUiEffect {
    data class Error(val gatewayError: GatewayError?) : ProductDetailUiEffect()
    data class StartScan(val product: Product) : ProductDetailUiEffect()
    data class AddingResultFailed(val reason: String) : ProductDetailUiEffect()
}