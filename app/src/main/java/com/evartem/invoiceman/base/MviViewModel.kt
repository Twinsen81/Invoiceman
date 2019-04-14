package com.evartem.invoiceman.base

import androidx.lifecycle.ViewModel
import com.evartem.invoiceman.util.stackToString
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * A base [ViewModel] class that implements the core MVI logic.
 * It accepts events from UI, processes them, and emits a UI state and/or UI effects.
 *
 * UI state is all the data that a UI layer (a fragment) needs to render itself properly.
 * US state survives configuration changes.
 *
 * UI effect is one time event that should be processed by the UI layer. This can be a message to the user
 * or a navigation request (open another fragment).
 *
 * @param UiState a class that represents a UI state
 * @param UiEffect a class that represents a UI effect
 * @param Event a class that represents an event from UI or some other source
 * @param ViewModelResult a class that represents an intermediate result of the events processing
 * @property startingEvent the event that is fired when the ViewModel is created
 * @property startingUiState the initial state of the UI that should be rendered when the ViewModel is created
 */
abstract class MviViewModel<UiState, UiEffect, Event, ViewModelResult>
    (private val startingEvent: Event, private val startingUiState: UiState) : ViewModel() {

    /**
     * Events from UI (or other sources) that this view model must process
     */
    private val events: PublishSubject<Event> = PublishSubject.create()

    /**
     * Indicates that the subscription to the [events] has already happened
     */
    @Volatile
    private var isInitialized: Boolean = false

    /**
     * The UI state observable that the UI layer should subscribe to in order to receive and render state updates.
     * Implemented as BehaviorSubject, thus it emits the last UI state each time the UI layer subscribes. This helps
     * retain the UI state on configuration changes.
     *
     * Upon the first UI layer subscription, initializes (if not yet) the internal subscription to the [events] observable.
     */
    val uiState: BehaviorSubject<UiState> = BehaviorSubject.create()
        get() {
            if (!isInitialized) subscribeAndProcessEvents()
            return field
        }

    /**
     * The UI effects observable that the UI layer should subscribe to in order to receive and render UI effects.
     * Implemented as PublishSubject, thus it emits effects only once and the same effect won't be received by UI on
     * configuration changes.
     *
     * Upon the first UI layer subscription, initializes (if not yet) the internal subscription to the [events] observable.
     */
    val uiEffects: PublishSubject<UiEffect> = PublishSubject.create()
        get() {
            if (!isInitialized) subscribeAndProcessEvents()
            return field
        }

    private lateinit var eventsDisposable: Disposable

    /**
     * Add new event for processing.
     *
     */
    fun addEvent(event: Event) = events.onNext(event)

    protected fun addUiEffect(uiEffect: UiEffect) = uiEffects.onNext(uiEffect)

    /**
     * The events processing chain. Each event is forwarded to [eventToResult] method.
     * Then, if the result affects the UI, it is sent to the reducer that produces a new UI state
     * and, possibly, UI effects. At the end, the new state is pushed to [uiState] to be consumed by
     * the UI layer.
     *
     * NOTE: the call to "scan" will make [uiState] emit its first value [startingUiState]
     * even before the first result comes from [startingEvent]. Thus, the fragment will
     * be initialized with [startingUiState] on its creation.
     */
    @Synchronized
    private fun subscribeAndProcessEvents() {
        if (isInitialized) return else isInitialized = true

        eventsDisposable = events
            .startWith(startingEvent)
            .doOnNext { Timber.d("MVI-Event: $it, thread: ${Thread.currentThread()}") }
            .flatMap { event -> eventToResult(event) }
            .filter { event -> shouldUpdateUiState(event) }
            .doOnNext { Timber.d("MVI-Result: $it, thread: ${Thread.currentThread()}") }
            .scan(startingUiState) { previousUiState, newResult ->
                reduceUiState(previousUiState, newResult)
            }
            .onErrorReturn { exception ->
                Timber.wtf("MVI-Critical app error while processing an event:\n${exception.stackToString()}")
                startingUiState
            }
            .subscribe(uiState::onNext)
    }

    /**
     * Process the event and return the result.
     */
    protected abstract fun eventToResult(event: Event): Observable<ViewModelResult>

    /**
     * Check if the produced result affects the UI layer. If not (the return value = false) then
     * no need to create a new UI state.
     */
    protected open fun shouldUpdateUiState(result: ViewModelResult): Boolean = true

    /**
     * Create a new UI state depending on the previous state an the new result.
     */
    protected abstract fun reduceUiState(previousUiState: UiState, newResult: ViewModelResult): UiState

    /**
     * Wrap the [Event] into [ViewModelResult] so it would go through the UI
     * reducer. The reducer will change the UI according to the event type.
     */
    protected abstract fun relay(event: Event): Observable<ViewModelResult>

    override fun onCleared() {
        super.onCleared()
        eventsDisposable.dispose()
    }
}