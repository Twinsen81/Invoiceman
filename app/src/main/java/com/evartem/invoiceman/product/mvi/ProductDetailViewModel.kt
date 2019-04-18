package com.evartem.invoiceman.product.mvi

import com.evartem.domain.entity.doc.Result
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.DeleteResultUseCase
import com.evartem.domain.interactor.GetProductUseCase
import com.evartem.domain.interactor.InsertOrUpdateResultUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable
import timber.log.Timber

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
            is ProductDetailEvent.FabClick -> onFabClicked(event)
            is ProductDetailEvent.AddResult -> onAddResult(event)
            is ProductDetailEvent.DeleteResult -> onDeleteResult(event)
            is ProductDetailEvent.EditResult,
            is ProductDetailEvent.Empty -> relay(event)
        }

    override fun shouldUpdateUiState(result: ProductDetailViewModelResult): Boolean =
        if (result is ProductDetailViewModelResult.RelayEvent)
            when (result.uiEvent) {
                is ProductDetailEvent.FabClick,
                is ProductDetailEvent.Empty -> false
                else -> true
            } else true

    private fun onLoadProductData(): Observable<ProductDetailViewModelResult> =
        getProductUseCase.execute(Pair(sessionManager.getInvoiceId(), sessionManager.getProductId()))
            .map { ProductDetailViewModelResult.Product(it) }

    private fun onFabClicked(event: ProductDetailEvent): Observable<ProductDetailViewModelResult> {
        addUiEffect(ProductDetailUiEffect.StartScan(uiState.value!!.product))
        return relay(event)
    }

    private fun onAddResult(event: ProductDetailEvent.AddResult): Observable<ProductDetailViewModelResult> {

        val resultWithId: Result?

        try {
            resultWithId =
                uiState.value!!.product.addResult(event.result.status, event.result.serial, event.result.comment)
        } catch (exception: IllegalArgumentException) {
            Timber.d(exception)
            addUiEffect(ProductDetailUiEffect.AddingResultFailed(exception.localizedMessage))
            return relay(ProductDetailEvent.Empty)
        }

        return insertOrUpdateResultUseCase.execute(
            Triple(
                sessionManager.getInvoiceId(),
                sessionManager.getProductId(),
                resultWithId
            )
        )
            .map { ProductDetailViewModelResult.ResultOperation(it) }
    }

    private fun onDeleteResult(event: ProductDetailEvent.DeleteResult): Observable<ProductDetailViewModelResult> =
        deleteResultUseCase.execute(
            Triple(
                sessionManager.getInvoiceId(),
                sessionManager.getProductId(),
                event.resultId
            )
        )
            .map { ProductDetailViewModelResult.ResultOperation(it) }

    override fun reduceUiState(
        previousUiState: ProductDetailUiState,
        newResult: ProductDetailViewModelResult
    ): ProductDetailUiState {
        val newUiState = previousUiState.copy()

        // region Received a response - the current product
        if (newResult is ProductDetailViewModelResult.Product) {
            newUiState.isLoading = false

            if (newResult.gatewayResult is InvoiceGatewayResult.Product)
                newUiState.product = newResult.gatewayResult.product

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(ProductDetailUiEffect.Error(newResult.gatewayResult.gatewayError))
        }
        // endregion

        // region Received a response - a result operation (add/delete/update)
        if (newResult is ProductDetailViewModelResult.ResultOperation) {
            if (newResult.gatewayResult is InvoiceGatewayResult.ResultOperationSucceeded)
                newUiState.product = newResult.gatewayResult.updatedProduct

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(ProductDetailUiEffect.Error(newResult.gatewayResult.gatewayError))
        }
        // endregion

        return newUiState
    }

    override fun relay(event: ProductDetailEvent): Observable<ProductDetailViewModelResult> =
        Observable.just(ProductDetailViewModelResult.RelayEvent(event))
}