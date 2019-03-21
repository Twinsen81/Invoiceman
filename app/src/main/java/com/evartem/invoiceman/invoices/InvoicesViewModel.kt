package com.evartem.invoiceman.invoices

import androidx.lifecycle.ViewModel
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class InvoicesViewModel: ViewModel() {

    val uiState: BehaviorSubject<InvoicesUiState> = BehaviorSubject.create()
    val uiEffects: PublishSubject<InvoicesUiEffect> = PublishSubject.create()

    private val events: PublishSubject<InvoicesEvent> = PublishSubject.create()
    private lateinit var eventsDisposable: Disposable

    init {
        processEvents()
    }

    fun addEvent(event: InvoicesEvent) = events.onNext(event)

    private fun processEvents() {
        Timber.d("MVI-Init")

        eventsDisposable = events
            .startWith(InvoicesEvent.LoadScreenEvent)
            .doOnNext { Timber.d("MVI-Event: $it") }
            .flatMap { event -> eventToResult(event) }
            .doOnNext { Timber.d("MVI-Result: $it") }
            .filter { result -> !processAsUiEffect(result) }
            .scan(InvoicesUiState()) {
                previousUiState, newResult -> reduceUiState(previousUiState, newResult)
            }
            .subscribe {
                uiState.onNext(it)
            }

        //TODO: Add error processing to the stream - onErrorReturn
    }

    private fun eventToResult(event: InvoicesEvent): Observable<InvoicesViewModelResult> =
        when (event) {
            is InvoicesEvent.LoadScreenEvent -> onLoadScreenEvent()
            is InvoicesEvent.RefreshScreenEvent -> onRefreshScreenEvent()
            is InvoicesEvent.SearchInvoiceEvent -> onSearchInvoiceEvent(event)
        }

    private fun onLoadScreenEvent(): Observable<InvoicesViewModelResult> =
        Observable.just(InvoicesViewModelResult.AllInvoicesResult(
            InvoiceGatewayResult.InvoicesRequestResult(listOf(),
                InvoiceGatewayResult.ResponseCode.DENIED_INCONSISTENT_DATA)))

    private fun onRefreshScreenEvent(): Observable<InvoicesViewModelResult> =
        Observable.just(InvoicesViewModelResult.AllInvoicesResult(
            InvoiceGatewayResult.InvoicesRequestResult(listOf(),
                InvoiceGatewayResult.ResponseCode.DENIED_NETWORK_ERROR)))

    private fun onSearchInvoiceEvent(event: InvoicesEvent.SearchInvoiceEvent): Observable<InvoicesViewModelResult> =
        Observable.just(InvoicesViewModelResult.SearchResult(event.searchQuery,
            InvoiceGatewayResult.InvoicesRequestResult(listOf(),
                InvoiceGatewayResult.ResponseCode.SUCCESS)))


    private fun reduceUiState(previousState: InvoicesUiState, newResult: InvoicesViewModelResult): InvoicesUiState {
        return InvoicesUiState(if (newResult is InvoicesViewModelResult.SearchResult) newResult.searchQuery else "",
            newResult.gatewayResult.response == InvoiceGatewayResult.ResponseCode.DENIED_NOT_FOUND)
    }

    private fun processAsUiEffect(result: InvoicesViewModelResult): Boolean {
        return if (result.gatewayResult.response == InvoiceGatewayResult.ResponseCode.DENIED_NETWORK_ERROR) {
            uiEffects.onNext(
                InvoicesUiEffect.NetworkError(result.gatewayResult.networkError?.code ?: 0,
                    result.gatewayResult.networkError?.message ?: "Unknown network error"))
            true
        }else
            false
    }

    override fun onCleared() {
        super.onCleared()
        eventsDisposable.dispose()
    }
}
