package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoiceUseCase
import com.evartem.domain.interactor.RequestInvoiceForProcessingUseCase
import com.evartem.domain.interactor.RequestInvoiceReturnUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable

/**
 * Displays the detailed info about an invoice, including the list of its products.
 */
class InvoiceDetailViewModel(
    private val sessionManager: SessionManager,
    private val getInvoiceUseCase: GetInvoiceUseCase,
    private val requestProcessingUseCase: RequestInvoiceForProcessingUseCase,
    private val requestReturnUseCase: RequestInvoiceReturnUseCase
) :
    MviViewModel<InvoiceDetailUiState, InvoiceDetailUiEffect, InvoiceDetailEvent, InvoiceDetailViewModelResult>(
        InvoiceDetailEvent.LoadScreen,
        InvoiceDetailUiState.EmptyUiState
    ) {

    override fun eventToResult(event: InvoiceDetailEvent): Observable<InvoiceDetailViewModelResult> =
        when (event) {
            is InvoiceDetailEvent.LoadScreen -> onLoadInvoiceData()
            is InvoiceDetailEvent.Click -> onProductClicked(event)
            is InvoiceDetailEvent.Accept -> Observable.merge(relay(event), onAcceptInvoiceClicked())
            is InvoiceDetailEvent.Return -> Observable.merge(relay(event), onReturnInvoiceClicked())
            is InvoiceDetailEvent.Submit,
            is InvoiceDetailEvent.Search,
            is InvoiceDetailEvent.Empty -> relay(event)
        }

    private fun onAcceptInvoiceClicked(): Observable<InvoiceDetailViewModelResult> =
        requestProcessingUseCase.execute(sessionManager.currentUser to sessionManager.currentInvoiceId)
            .map { InvoiceDetailViewModelResult.AcceptRequest(it) }

    private fun onReturnInvoiceClicked(): Observable<InvoiceDetailViewModelResult> =
        requestReturnUseCase.execute(sessionManager.currentUser to sessionManager.currentInvoiceId)
            .map { InvoiceDetailViewModelResult.ReturnRequest(it) }

    override fun shouldUpdateUiState(result: InvoiceDetailViewModelResult): Boolean =
        if (result is InvoiceDetailViewModelResult.RelayEvent)
            when (result.uiEvent) {
                is InvoiceDetailEvent.Click,
                is InvoiceDetailEvent.Empty -> false
                else -> true
            } else true

    private fun onProductClicked(event: InvoiceDetailEvent.Click): Observable<InvoiceDetailViewModelResult> {
        if (uiState.value?.isBeingProcessedByUser == true)
            addUiEffect(InvoiceDetailUiEffect.ProductClick(event.productId))
        else
            addUiEffect(InvoiceDetailUiEffect.NotAcceptedYetMessage)
        return relay(event)
    }

    private fun onLoadInvoiceData(): Observable<InvoiceDetailViewModelResult> =
        getInvoiceUseCase.execute(sessionManager.currentInvoiceId)
            .map { InvoiceDetailViewModelResult.Invoice(it) }

    override fun reduceUiState(
        previousUiState: InvoiceDetailUiState,
        newResult: InvoiceDetailViewModelResult
    ): InvoiceDetailUiState {
        val newUiState = previousUiState.copy()

        // region Received a response - the invoice
        if (newResult is InvoiceDetailViewModelResult.Invoice) {
            newUiState.isLoadingInvoice = false

            if (newResult.gatewayResult is InvoiceGatewayResult.Invoice)
                newUiState.invoice = newResult.gatewayResult.invoice

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoiceDetailUiEffect.Error(newResult.gatewayResult.gatewayError))
        }
        // endregion

        // region Received a response - the ACCEPT request
        if (newResult is InvoiceDetailViewModelResult.AcceptRequest) {

            if (newResult.gatewayResult is InvoiceGatewayResult.AcceptConfirmed) {
                newUiState.invoice.processedByUser = sessionManager.currentUser.id
                addUiEffect(InvoiceDetailUiEffect.SuccessMessage)
            }

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoiceDetailUiEffect.Error(newResult.gatewayResult.gatewayError))

            newUiState.isRequestingAccept = false
        }
        // endregion

        // region Received a response - the RETURN request
        if (newResult is InvoiceDetailViewModelResult.ReturnRequest) {

            if (newResult.gatewayResult is InvoiceGatewayResult.ReturnConfirmed) {
                newUiState.invoice.processedByUser = ""
                addUiEffect(InvoiceDetailUiEffect.SuccessMessage)
            }

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoiceDetailUiEffect.Error(newResult.gatewayResult.gatewayError))

            newUiState.isRequestingReturn = false
        }
        // endregion

        // region Relaying events
        if (newResult is InvoiceDetailViewModelResult.RelayEvent) {
            // Search
            if (newResult.uiEvent is InvoiceDetailEvent.Search) {
                when {
                    newResult.uiEvent.stopSearch -> {
                        newUiState.setFocusToSearchView = false
                        newUiState.searchRequest = ""
                        newUiState.searchViewOpen = false
                    }
                    newResult.uiEvent.startSearch -> {
                        newUiState.setFocusToSearchView = true
                        newUiState.searchViewOpen = true
                    }
                    else -> {
                        newUiState.setFocusToSearchView = false
                        newUiState.searchRequest = newResult.uiEvent.searchQuery
                    }
                }
            }

            if (newResult.uiEvent is InvoiceDetailEvent.Accept)
                newUiState.isRequestingAccept = true

            if (newResult.uiEvent is InvoiceDetailEvent.Return)
                newUiState.isRequestingReturn = true
        }
        // endregion

        newUiState.isBeingProcessedByUser =
            newUiState.invoice.processedByUser?.equals(sessionManager.currentUser.id) == true

        return newUiState
    }

    override fun relay(event: InvoiceDetailEvent): Observable<InvoiceDetailViewModelResult> =
        Observable.just(InvoiceDetailViewModelResult.RelayEvent(event))
}