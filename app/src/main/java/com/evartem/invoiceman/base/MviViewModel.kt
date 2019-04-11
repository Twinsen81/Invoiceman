package com.evartem.invoiceman.base

import androidx.lifecycle.ViewModel
import com.evartem.domain.interactor.Schedulers
import com.evartem.invoiceman.util.stackToString
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

abstract class MviViewModel<UiState, UiEffect, Event, ViewModelResult>
    (private val schedulers: Schedulers, private val startingEvent: Event, private val startingUiState: UiState) : ViewModel() {

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

    protected fun addUiEffect(uiEffect: UiEffect) = uiEffects.onNext(uiEffect)

    /**
     * NOTE: the call to "scan" will make [uiState] emit its first value [startingUiState]
     * even before the first result comes from [startingEvent]. Thus, the fragment will
     * be initialized with [startingUiState] on its creation.
     */
    @Synchronized
    private fun subscribeAndProcessEvents() {
        if (isInitialized) return else isInitialized = true

        Timber.d("MVI-Init")

        eventsDisposable = events
//            .subscribeOn(schedulers.subscribeOn)
//            .observeOn(schedulers.observeOn)
            .startWith(startingEvent)
            .doOnNext { Timber.d("MVI-Event: $it, thread: .doOnNext ${Thread.currentThread()}") }
            .flatMap { event -> eventToResult(event) }
            .filter { event -> shouldUpdateUiState(event) }
            .doOnNext { Timber.d("MVI-Result: $it, thread: .doOnNext ${Thread.currentThread()}") }
            .scan(startingUiState) { previousUiState, newResult ->
                reduceUiState(previousUiState, newResult)
            }
            .onErrorReturn { exception ->
                Timber.wtf("MVI-Critical app error while processing an event:\n${exception.stackToString()}")
                startingUiState
            }
            .subscribe(uiState::onNext)
    }

    protected abstract fun eventToResult(event: Event): Observable<ViewModelResult>

    protected open fun shouldUpdateUiState(result: ViewModelResult): Boolean = true

    protected abstract fun reduceUiState(previousUiState: UiState, newResult: ViewModelResult): UiState

    override fun onCleared() {
        super.onCleared()
        eventsDisposable.dispose()
    }
}