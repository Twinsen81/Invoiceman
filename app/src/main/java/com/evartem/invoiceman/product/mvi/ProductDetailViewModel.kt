package com.evartem.invoiceman.product.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.DeleteResultUseCase
import com.evartem.domain.interactor.GetProductUseCase
import com.evartem.domain.interactor.InsertOrUpdateResultUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable

/**
 * Displays the detailed info about an invoice, including the list of its products.
 */
class ProductDetailViewModel(
    private val sessionManager: SessionManager,
    private val getProductUseCase: GetProductUseCase,
    private val insertOrUpdateResultUseCase: InsertOrUpdateResultUseCase,
    private val deleteResultUseCase: DeleteResultUseCase
) :
    MviViewModel<ProductDetailUiState, ProductDetailUiEffect, ProductDetailEvent, ProductDetailViewModelResult>(
        ProductDetailEvent.LoadScreen,
        ProductDetailUiState.EmptyUiState
    ) {

    override fun eventToResult(event: ProductDetailEvent): Observable<ProductDetailViewModelResult> =
        when (event) {
            is ProductDetailEvent.LoadScreen -> onLoadProductData()
            is ProductDetailEvent.Click,
            is ProductDetailEvent.Empty -> relay(event)
        }

    override fun shouldUpdateUiState(result: ProductDetailViewModelResult): Boolean =
        if (result is ProductDetailViewModelResult.RelayEvent)
            when (result.uiEvent) {
                is ProductDetailEvent.Click,
                is ProductDetailEvent.Empty -> false
                else -> true
            } else true

    private fun onLoadProductData(): Observable<ProductDetailViewModelResult> =
        getProductUseCase.execute(Pair(sessionManager.currentInvoiceId, sessionManager.currentProductId))
            .map { ProductDetailViewModelResult.Product(it) }

    override fun reduceUiState(
        previousUiState: ProductDetailUiState,
        newResult: ProductDetailViewModelResult
    ): ProductDetailUiState {
        val newUiState = previousUiState.copy()

        // region Received a response - the invoice
        if (newResult is ProductDetailViewModelResult.Product) {
            newUiState.isLoading = false

            if (newResult.gatewayResult is InvoiceGatewayResult.Product)
                newUiState.product = newResult.gatewayResult.product

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(ProductDetailUiEffect.Error(newResult.gatewayResult.gatewayError))
        }
        // endregion

        return newUiState
    }

    override fun relay(event: ProductDetailEvent): Observable<ProductDetailViewModelResult> =
        Observable.just(ProductDetailViewModelResult.RelayEvent(event))
}