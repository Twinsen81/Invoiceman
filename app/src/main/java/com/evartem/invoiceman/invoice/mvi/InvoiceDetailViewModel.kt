package com.evartem.invoiceman.invoice.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoiceUseCase
import com.evartem.domain.interactor.RequestInvoiceForProcessingUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable

class InvoiceDetailViewModel(
    private val sessionManager: SessionManager,
    private val getInvoiceUseCase: GetInvoiceUseCase,
    private val requestProcessingUseCase: RequestInvoiceForProcessingUseCase
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
            is InvoiceDetailEvent.Return,
            is InvoiceDetailEvent.Submit,
            is InvoiceDetailEvent.Search,
            is InvoiceDetailEvent.Empty -> relay(event)
        }

    private fun onAcceptInvoiceClicked(): Observable<InvoiceDetailViewModelResult> =
    requestProcessingUseCase.execute(sessionManager.currentUser to sessionManager.currentInvoiceId)
        .map { InvoiceDetailViewModelResult.AcceptRequest(it) }

    override fun shouldUpdateUiState(result: InvoiceDetailViewModelResult): Boolean =
        if (result is InvoiceDetailViewModelResult.RelayEvent)
            when (result.uiEvent) {
                is InvoiceDetailEvent.Click,
                is InvoiceDetailEvent.Empty -> false
                else -> true
            } else true

    private fun onProductClicked(event: InvoiceDetailEvent.Click): Observable<InvoiceDetailViewModelResult> {
        addUiEffect(InvoiceDetailUiEffect.ProductClick(event.productId))
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

        // Received a response - the invoice
        if (newResult is InvoiceDetailViewModelResult.Invoice) {
            newUiState.isLoadingInvoice = false

            if (newResult.gatewayResult is InvoiceGatewayResult.Invoice)
                newUiState.invoice = newResult.gatewayResult.invoice

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoiceDetailUiEffect.Error(newResult.gatewayResult.gatewayError))
        }

        // Received a response - the ACCEPT request
        if (newResult is InvoiceDetailViewModelResult.AcceptRequest) {

            if (newResult.gatewayResult is InvoiceGatewayResult.ProcessingAcceptConfirmed)
                newUiState.invoice.processedByUser = sessionManager.currentUser.id

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoiceDetailUiEffect.Error(newResult.gatewayResult.gatewayError))
     /*       newUiState.isLoadingInvoice = false

            if (newResult.gatewayResult is InvoiceGatewayResult.Invoice)
                newUiState.invoice = newResult.gatewayResult.invoice

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoiceDetailUiEffect.Error(newResult.gatewayResult.gatewayError))*/
        }

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
        }

        return newUiState
    }

    private fun relay(event: InvoiceDetailEvent): Observable<InvoiceDetailViewModelResult> =
        Observable.just(InvoiceDetailViewModelResult.RelayEvent(event))
}