package com.evartem.invoiceman.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

abstract class MviViewModel<UiState, UiEffect, Event, ViewModelResult>
    (private val startingEvent: Event, private val startingUiState: UiState) : ViewModel() {

    val uiState: BehaviorSubject<UiState> = BehaviorSubject.create()
    val uiEffects: PublishSubject<UiEffect> = PublishSubject.create()

    private val events: PublishSubject<Event> = PublishSubject.create()
    private lateinit var eventsDisposable: Disposable

    init {
        processEvents()
    }

    fun addEvent(event: Event) = events.onNext(event)

    private fun processEvents() {
        Timber.d("MVI-Init")

        eventsDisposable = events
            .startWith(startingEvent)
            .doOnNext { Timber.d("MVI-Event: $it") }
            .flatMap { event -> eventToResult(event) }
            .doOnNext { Timber.d("MVI-Result: $it") }
            .filter { result -> !processAsUiEffect(result) }
            .scan(startingUiState) { previousUiState, newResult ->
                reduceUiState(previousUiState, newResult)
            }
            .subscribe(uiState::onNext) { Timber.wtf("MVI-Critical app error while an event") }
    }

    private fun processAsUiEffect(result: ViewModelResult): Boolean {
        val uiEffect = getUiEffect(result)
        if (uiEffect != null) {
            uiEffects.onNext(uiEffect)
            return true
        }
        return false
    }

    protected abstract fun eventToResult(event: Event): Observable<ViewModelResult>

    protected abstract fun reduceUiState(previousUiState: UiState, newResult: ViewModelResult): UiState

    protected abstract fun getUiEffect(newResult: ViewModelResult): UiEffect?

    override fun onCleared() {
        super.onCleared()
        eventsDisposable.dispose()
    }
}