package com.evartem.invoiceman.invoice_detail.mvi

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoiceUseCase
import com.evartem.invoiceman.base.MviViewModel
import io.reactivex.Observable

class InvoiceDetailViewModel(private val user: User, private val invoiceId: String, private val getInvoiceUseCase: GetInvoiceUseCase) :
    MviViewModel<InvoiceDetailUiState, InvoiceDetailUiEffect, InvoiceDetailEvent, InvoiceDetailViewModelResult>(
        InvoiceDetailEvent.LoadScreen,
        InvoiceDetailUiState.EmptyUiState
    ) {

    override fun eventToResult(event: InvoiceDetailEvent): Observable<InvoiceDetailViewModelResult> =
        when (event) {
            is InvoiceDetailEvent.LoadScreen -> onLoadInvoiceData()
            is InvoiceDetailEvent.Empty -> relay(event)
        }

    private fun onLoadInvoiceData(): Observable<InvoiceDetailViewModelResult> =
        getInvoiceUseCase.execute(invoiceId)
            .map {
                InvoiceDetailViewModelResult.Invoice(it)
            }

    override fun reduceUiState(previousUiState: InvoiceDetailUiState, newResult: InvoiceDetailViewModelResult): InvoiceDetailUiState {
        val newUiState = previousUiState.copy()

        // Received a response from the repository
        if (newResult is InvoiceDetailViewModelResult.Invoice &&
            newResult.gatewayResult is InvoiceGatewayResult.InvoiceRequestResult
        ) {
            newUiState.isLoading = false
            if (newResult.gatewayResult.success)
                newUiState.invoice = newResult.gatewayResult.invoice
            else
                addUiEffect(InvoiceDetailUiEffect.RemoteDatasourceError(newResult.gatewayResult.gatewayError))
        }

        return newUiState
    }

    private fun relay(event: InvoiceDetailEvent): Observable<InvoiceDetailViewModelResult> =
        Observable.just(InvoiceDetailViewModelResult.RelayEvent(event))
}