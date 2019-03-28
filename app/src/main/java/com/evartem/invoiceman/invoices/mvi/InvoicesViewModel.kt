package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.GatewayErrorCode
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.base.MviViewModel
import io.reactivex.Observable

class InvoicesViewModel(private val user: User, private val getInvoicesForUserUseCase: GetInvoicesForUserUseCase) :
    MviViewModel<InvoicesUiState, InvoicesUiEffect, InvoicesEvent, InvoicesViewModelResult>(
        InvoicesEvent.LoadScreenEvent,
        InvoicesUiState(isLoading = true)
    ) {

    override fun eventToResult(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        when (event) {
            is InvoicesEvent.LoadScreenEvent -> onLoadScreenEvent()
            is InvoicesEvent.RefreshScreenEvent -> onRefreshScreenEvent()
            is InvoicesEvent.SearchInvoiceEvent -> onSearchInvoiceEvent(event)
        }

    private fun onLoadScreenEvent(): Observable<InvoicesViewModelResult> =
        getInvoicesForUserUseCase.execute(Pair(user, true))
            .map {
                InvoicesViewModelResult.Invoices(it)
            }

    private fun onRefreshScreenEvent(): Observable<InvoicesViewModelResult> =
        Observable.merge(
            Observable.just(InvoicesViewModelResult.IsRefreshing()),
            getInvoicesForUserUseCase.execute(Pair(user, true))
                .map {
                    InvoicesViewModelResult.Invoices(it)
                })

    private fun onSearchInvoiceEvent(event: InvoicesEvent.SearchInvoiceEvent): Observable<InvoicesViewModelResult> =
        Observable.just(
            InvoicesViewModelResult.Search(
                event.searchQuery,
                InvoiceGatewayResult.InvoicesRequestResult(
                    listOf(),
                    true
                )
            )
        )

    override fun reduceUiState(previousUiState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState =
        when (newResult) {
            is InvoicesViewModelResult.Invoices -> {

                lateinit var newUiState: InvoicesUiState

                when (newResult.gatewayResult) {
                    is InvoiceGatewayResult.InvoicesRequestResult -> {
                        if (newResult.gatewayResult.success)
                            newUiState = InvoicesUiState(invoices = newResult.gatewayResult.invoices)
                        else {
                            newUiState = previousUiState.copy(isLoading = false, isRefreshing = false)
                            addUiEffect(
                                InvoicesUiEffect.RemoteDatasourceError(
                                    newResult.gatewayResult.gatewayError ?: GatewayError(
                                        GatewayErrorCode.UNKNOWN_ERROR,
                                        "Unknown network error"
                                    )
                                )
                            )
                        }
                    }
                    else -> newUiState = previousUiState.copy(isLoading = false, isRefreshing = false)
                }

                if (newResult.gatewayResult.success && previousUiState.isRefreshing &&
                    previousUiState.invoices == newUiState.invoices
                )
                    addUiEffect(InvoicesUiEffect.NoNewData())

                newUiState
            }
            is InvoicesViewModelResult.IsRefreshing -> previousUiState.copy(isRefreshing = true)
            else -> previousUiState.copy(isLoading = false, isRefreshing = false)
        }
}