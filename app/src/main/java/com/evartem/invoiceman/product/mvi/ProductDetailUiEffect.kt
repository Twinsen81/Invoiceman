package com.evartem.invoiceman.product.mvi

import com.evartem.domain.gateway.GatewayError

sealed class ProductDetailUiEffect {
    data class Error(val gatewayError: GatewayError?) : ProductDetailUiEffect()
}