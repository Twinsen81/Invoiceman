package com.evartem.invoiceman.invoices.mvi

import android.view.SearchEvent
import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.GatewayErrorCode
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.base.MviViewModel
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
            is InvoicesEvent.Search -> relay(event)
        }

    private fun onRefreshData(): Observable<InvoicesViewModelResult> =
        getInvoicesForUserUseCase.execute(Pair(user, true))
            .map {
                InvoicesViewModelResult.Invoices(it)
            }

    override fun reduceUiState(previousUiState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState {
        val newUiState = previousUiState.copy()

        if (newResult is InvoicesViewModelResult.Invoices &&
            newResult.gatewayResult is InvoiceGatewayResult.InvoicesRequestResult
        ) {
            newUiState.isRefreshing = false
            newUiState.isLoading = false
            if (newResult.gatewayResult.success)
                newUiState.invoices = newResult.gatewayResult.invoices
            else
                addUiEffect(InvoicesUiEffect.RemoteDatasourceError(newResult.gatewayResult.gatewayError))

            if (newResult.gatewayResult.success && previousUiState.isRefreshing &&
                previousUiState.invoices == newResult.gatewayResult.invoices
            )
                addUiEffect(InvoicesUiEffect.NoNewData())
        }

        newUiState.isInvoicesChanged = newUiState.invoices != previousUiState.invoices

        if (newResult is InvoicesViewModelResult.RelayEvent) {
            newUiState.isRefreshing = newResult.uiEvent is InvoicesEvent.RefreshScreen

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

            if (newUiState.isRefreshing) newUiState.searchViewOpen = false
        }
        return newUiState
    }

    private fun relay(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        Observable.just(InvoicesViewModelResult.RelayEvent(event))
}