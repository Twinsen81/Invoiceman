package com.evartem.invoiceman.product.mvi

import com.evartem.domain.entity.doc.Result

sealed class ProductDetailEvent {
    object Empty : ProductDetailEvent()
    object LoadScreen : ProductDetailEvent()
    object FabClick : ProductDetailEvent()
    data class AddResult(val result: Result) : ProductDetailEvent()
    data class DeleteResult(val resultId: Int) : ProductDetailEvent()
    data class EditResult(val resultId: Int) : ProductDetailEvent()
}
