package com.evartem.invoiceman.base

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

abstract class MviViewModel<UiState, UiEffect, Event, ViewModelResult>
    (private val startingEvent: Event, private val startingUiState: UiState) : ViewModel() {

    private val events: PublishSubject<Event> = PublishSubject.create()

    @Volatile
    private var isInitialized: Boolean = false

    val uiState: BehaviorSubject<UiState> = BehaviorSubject.create()
        get() {
            if (!isInitialized) subscribeAndProcessEvents()
            return field
        }

    val uiEffects: PublishSubject<UiEffect> = PublishSubject.create()
        get() {
            if (!isInitialized) subscribeAndProcessEvents()
            return field
        }

    private lateinit var eventsDisposable: Disposable

    fun addEvent(event: Event) = events.onNext(event)

    @Synchronized
    private fun subscribeAndProcessEvents() {
        if (isInitialized) return else isInitialized = true

        Timber.d("MVI-Init")

        eventsDisposable = events
            .startWith(startingEvent)
            .doOnNext { Timber.d("MVI-Event: ${it.toString()}") }
            .flatMap { event -> eventToResult(event) }
            .doOnNext { Timber.d("MVI-Result: ${it.toString()}") }
            .filter { result -> !processAsUiEffect(result) }
            .scan(startingUiState) { previousUiState, newResult ->
                reduceUiState(previousUiState, newResult)
            }
            .onErrorReturn { error ->
                Timber.wtf("MVI-Critical app error while processing an event:\n${error.localizedMessage}")
                startingUiState
            }
            .subscribe(uiState::onNext)
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