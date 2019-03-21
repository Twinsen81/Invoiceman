package com.evartem.invoiceman.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

abstract class MviViewModel<UiState, UiEffect, UiEvent, ViewModelResult>(private val startingEvent: UiEvent,
                                                                       private val startingUiState: UiState): ViewModel()  {

    private val uiState: BehaviorSubject<UiState> = BehaviorSubject.create()
    private val uiEffects: PublishSubject<UiEffect> = PublishSubject.create()

    private val events: PublishSubject<UiEvent> = PublishSubject.create()
    private lateinit var eventsDisposable: Disposable

    init {
        processEvents()
    }

    fun addEvent(event: UiEvent) = events.onNext(event)

    protected fun addUiEffect(effect: UiEffect) = uiEffects.onNext(effect)

    private fun processEvents() {
        Timber.d("MVI-Init")

        eventsDisposable = events
            .startWith(startingEvent)
            .doOnNext { Timber.d("MVI-Event: $it") }
            .flatMap { event -> eventToResult(event) }
            .doOnNext { Timber.d("MVI-Result: $it") }
            .filter { result -> !processAsUiEffect(result) }
            .scan(startingUiState) {
                    previousUiState, newResult -> reduceUiState(previousUiState, newResult)
            }
            .subscribe {
                uiState.onNext(it)
            }

        //TODO: Add error processing to the stream - onErrorReturn
    }

    private fun processAsUiEffect(result: ViewModelResult): Boolean {
        val uiEffect = getUiEffect(result)
        if (uiEffect != null) {
            uiEffects.onNext(uiEffect)
            return true
        }
        return false
    }

    protected abstract fun eventToResult(event: UiEvent): Observable<ViewModelResult>

    protected abstract fun reduceUiState(previousState: UiState, newResult: ViewModelResult): UiState

    protected abstract fun getUiEffect(newResult: ViewModelResult): UiEffect?

    override fun onCleared() {
        super.onCleared()
        eventsDisposable.dispose()
    }

}