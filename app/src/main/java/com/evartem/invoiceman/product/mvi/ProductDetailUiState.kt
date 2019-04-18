package com.evartem.invoiceman.product.mvi

import com.evartem.domain.entity.doc.Product

data class ProductDetailUiState(
    var product: Product,
    var isLoading: Boolean = false,
    var displayProgress: Boolean = false,
    var progress: Pair<Int, Int> = 0 to 0
) {
    override fun toString(): String {
        return StringBuilder()
            .append("product_id=").append(product.id)
            .append(" isLoading=").append(isLoading)
            .append(" products=${product.getResults().size}").toString()
    }

    companion object {
        val EmptyUiState
            get() = ProductDetailUiState(Product(0, "", "", 0), isLoading = true)
    }
}