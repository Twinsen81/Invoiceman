package com.evartem.invoiceman.invoices

import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.invoiceman.base.MviViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModelResult
import io.reactivex.Observable

class InvoicesViewModel :
    MviViewModel<InvoicesUiState, InvoicesUiEffect, InvoicesEvent, InvoicesViewModelResult>(
        InvoicesEvent.LoadScreenEvent,
        InvoicesUiState()
    ) {

    override fun eventToResult(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        when (event) {
            is InvoicesEvent.LoadScreenEvent -> onLoadScreenEvent()
            is InvoicesEvent.RefreshScreenEvent -> onRefreshScreenEvent()
            is InvoicesEvent.SearchInvoiceEvent -> onSearchInvoiceEvent(event)
        }

    private fun onLoadScreenEvent(): Observable<InvoicesViewModelResult> =
        Observable.just(
            InvoicesViewModelResult.AllInvoicesResult(
                InvoiceGatewayResult.InvoicesRequestResult(
                    listOf(),
                    InvoiceGatewayResult.ResponseCode.DENIED_INCONSISTENT_DATA
                )
            )
        )

    private fun onRefreshScreenEvent(): Observable<InvoicesViewModelResult> =
        Observable.just(
            InvoicesViewModelResult.AllInvoicesResult(
                InvoiceGatewayResult.InvoicesRequestResult(
                    listOf(),
                    InvoiceGatewayResult.ResponseCode.DENIED_NETWORK_ERROR
                )
            )
        )

    private fun onSearchInvoiceEvent(event: InvoicesEvent.SearchInvoiceEvent): Observable<InvoicesViewModelResult> =
        Observable.just(
            InvoicesViewModelResult.SearchResult(
                event.searchQuery,
                InvoiceGatewayResult.InvoicesRequestResult(
                    listOf(),
                    InvoiceGatewayResult.ResponseCode.SUCCESS
                )
            )
        )

    override fun reduceUiState(previousUiState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState {
        return InvoicesUiState(
            if (newResult is InvoicesViewModelResult.SearchResult) newResult.searchQuery else "",
            newResult.gatewayResult.response == InvoiceGatewayResult.ResponseCode.DENIED_NOT_FOUND
        )
    }

    override fun getUiEffect(newResult: InvoicesViewModelResult): InvoicesUiEffect? {
        return if (newResult.gatewayResult.response == InvoiceGatewayResult.ResponseCode.DENIED_NETWORK_ERROR) {
            InvoicesUiEffect.NetworkError(
                newResult.gatewayResult.networkError?.code ?: 0,
                newResult.gatewayResult.networkError?.message ?: "Unknown network error"
            )
        } else
            null
    }
}