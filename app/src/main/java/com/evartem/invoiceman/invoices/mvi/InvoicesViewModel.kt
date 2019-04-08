package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.DateParser
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable
import timber.log.Timber

class InvoicesViewModel(
    private val sessionManager: SessionManager,
    private val getInvoicesForUserUseCase: GetInvoicesForUserUseCase
) :
    MviViewModel<InvoicesUiState, InvoicesUiEffect, InvoicesEvent, InvoicesViewModelResult>(
        InvoicesEvent.LoadScreen(reloadFromServer = true),
        InvoicesUiState(isLoading = true)
    ) {

    init {
        Timber.d("lifecycle init model")
    }

    override fun eventToResult(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        when (event) {
            is InvoicesEvent.LoadScreen -> onRefreshData(event.reloadFromServer)
            is InvoicesEvent.RefreshScreen -> Observable.merge(relay(event), onRefreshData())
            is InvoicesEvent.Click -> onInvoiceClicked(event)
            is InvoicesEvent.Search,
            is InvoicesEvent.Sort,
            is InvoicesEvent.Empty -> relay(event)
        }

    override fun shouldUpdateUiState(result: InvoicesViewModelResult): Boolean =
        if (result is InvoicesViewModelResult.RelayEvent)
            when (result.uiEvent) {
                is InvoicesEvent.Click,
                is InvoicesEvent.Empty -> false
                else -> true
            } else true

    private fun onRefreshData(reloadFromServer: Boolean = true): Observable<InvoicesViewModelResult> =
        getInvoicesForUserUseCase.execute(Pair(sessionManager.currentUser, reloadFromServer))
            .map {
                InvoicesViewModelResult.Invoices(it)
            }

    private fun onInvoiceClicked(event: InvoicesEvent.Click): Observable<InvoicesViewModelResult> {
        addUiEffect(InvoicesUiEffect.InvoiceClick(event.invoiceId))
        return relay(event)
    }

    override fun reduceUiState(previousUiState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState {

        val newUiState = previousUiState.copy()
        var responseWithDataReceived = false

        // Received a response - the invoices
        if (newResult is InvoicesViewModelResult.Invoices) {
            newUiState.isRefreshing = false
            newUiState.isLoading = false

            if (newResult.gatewayResult is InvoiceGatewayResult.Invoices) {
                newUiState.invoices = newResult.gatewayResult.invoices.toMutableList()
                newResult.gatewayResult.gatewayError?.let {
                    addUiEffect(InvoicesUiEffect.Error(it))
                }
            }

            if (newResult.gatewayResult is InvoiceGatewayResult.Error)
                addUiEffect(InvoicesUiEffect.Error(newResult.gatewayResult.gatewayError))

            responseWithDataReceived = newResult.gatewayResult is InvoiceGatewayResult.Invoices &&
                    previousUiState.isRefreshing
        }

        if (newResult is InvoicesViewModelResult.RelayEvent) {
            // Search
            if (newResult.uiEvent is InvoicesEvent.Search) {
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

            // Change sorting
            if (newResult.uiEvent is InvoicesEvent.Sort)
                newUiState.sorting = newResult.uiEvent.sortBy

            // Refreshing
            newUiState.isRefreshing = newResult.uiEvent is InvoicesEvent.RefreshScreen
            if (newUiState.isRefreshing) newUiState.searchViewOpen = false
        }

        // Sort
        when (newUiState.sorting) {
            SortInvoicesBy.NUMBER ->
                newUiState.invoices.sortWith(
                    compareBy(
                        { it.number },
                        { DateParser.toLocalDate(it.date) },
                        { it.seller })
                )
            SortInvoicesBy.DATE ->
                newUiState.invoices.sortWith(
                    compareBy(
                        { DateParser.toLocalDate(it.date) },
                        { it.number },
                        { it.seller })
                )
            SortInvoicesBy.SELLER ->
                newUiState.invoices.sortWith(
                    compareBy(
                        { it.seller },
                        { it.number },
                        { DateParser.toLocalDate(it.date) })
                )
            else -> Unit
        }

        if (responseWithDataReceived && previousUiState.invoices == newUiState.invoices)
            addUiEffect(InvoicesUiEffect.NoNewData())

        return newUiState
    }

    private fun relay(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        Observable.just(InvoicesViewModelResult.RelayEvent(event))
}