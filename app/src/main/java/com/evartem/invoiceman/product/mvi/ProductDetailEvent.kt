package com.evartem.invoiceman.product.mvi

sealed class ProductDetailEvent {
    object Empty : ProductDetailEvent()
    object Click : ProductDetailEvent()
    object LoadScreen : ProductDetailEvent()
}
