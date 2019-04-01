package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.DateParser
import io.reactivex.Observable

class InvoicesViewModel(private val user: User, private val getInvoicesForUserUseCase: GetInvoicesForUserUseCase) :
    MviViewModel<InvoicesUiState, InvoicesUiEffect, InvoicesEvent, InvoicesViewModelResult>(
        InvoicesEvent.LoadScreen,
        InvoicesUiState(isLoading = true)
    ) {

    override fun eventToResult(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        when (event) {
            is InvoicesEvent.LoadScreen -> onRefreshData()
            is InvoicesEvent.RefreshScreen -> Observable.merge(relay(event), onRefreshData())
            is InvoicesEvent.Search,
            is InvoicesEvent.Sort,
            is InvoicesEvent.Empty -> relay(event)
        }

    private fun onRefreshData(): Observable<InvoicesViewModelResult> =
        getInvoicesForUserUseCase.execute(Pair(user, true))
            .map {
                InvoicesViewModelResult.Invoices(it)
            }

    override fun reduceUiState(previousUiState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState {

        val newUiState = previousUiState.copy()
        var responseWithDataReceived = false

        // Received a response from the repository
        if (newResult is InvoicesViewModelResult.Invoices &&
            newResult.gatewayResult is InvoiceGatewayResult.InvoicesRequestResult
        ) {
            newUiState.isRefreshing = false
            newUiState.isLoading = false
            if (newResult.gatewayResult.success)
                newUiState.invoices = newResult.gatewayResult.invoices.toMutableList()
            else
                addUiEffect(InvoicesUiEffect.RemoteDatasourceError(newResult.gatewayResult.gatewayError))

            responseWithDataReceived = newResult.gatewayResult.success && previousUiState.isRefreshing
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