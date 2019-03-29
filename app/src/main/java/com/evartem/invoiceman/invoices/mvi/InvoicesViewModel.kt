package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.util.DateParser
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable

class InvoicesViewModel(
    private val sessionManager: SessionManager,
    private val getInvoicesForUserUseCase: GetInvoicesForUserUseCase
) :
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
        getInvoicesForUserUseCase.execute(Pair(sessionManager.currentUser, true))
            .map {
                InvoicesViewModelResult.Invoices(it)
            }

    override fun reduceUiState(previousUiState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState {

        val newUiState = previousUiState.copy()

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

            if (newResult.gatewayResult.success && previousUiState.isRefreshing &&
                previousUiState.invoices == newResult.gatewayResult.invoices
            )
                addUiEffect(InvoicesUiEffect.NoNewData())
        }

        if (newResult is InvoicesViewModelResult.RelayEvent) {
            // Search
            if (newResult.uiEvent is InvoicesEvent.Search) {
                when {
                    newResult.uiEvent.stopSearch -> {
                        newUiState.searchRequest = ""
                        newUiState.searchViewOpen = false
                    }
                    newResult.uiEvent.startSearch -> newUiState.searchViewOpen = true
                    else -> newUiState.searchRequest = newResult.uiEvent.searchQuery
                }
            }
            // Sort
            if (newResult.uiEvent is InvoicesEvent.Sort) {
                when (newResult.uiEvent.sortBy) {
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

                newUiState.isSortingChanged = true
            } else
                newUiState.isSortingChanged = false

            newUiState.isInvoicesChanged = newUiState.invoices.isNotEmpty() &&
                    (newUiState.invoices != previousUiState.invoices || newUiState.isSortingChanged)

            // Refreshing
            newUiState.isRefreshing = newResult.uiEvent is InvoicesEvent.RefreshScreen
            if (newUiState.isRefreshing) newUiState.searchViewOpen = false
        }
        return newUiState
    }

    private fun relay(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        Observable.just(InvoicesViewModelResult.RelayEvent(event))
}