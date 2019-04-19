package com.evartem.invoiceman.base

import androidx.fragment.app.Fragment
import com.evartem.invoiceman.util.stackToString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

/**
 * A base class that implements the core MVI logic for a Fragment-based UI layer.
 * It provides overrides for subscribing to an MVI view model class, rendering a UI state and UI effects.
 * The class also automatically subscribes, unsubscribes and disposes subscriptions according to
 * the fragment's lifecycle.
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
 */
abstract class MviFragment<UiState, UiEffect, Event> : Fragment() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    /**
     * The UI events (observables) of the concrete implementation are stored here.
     */
    private val uiEvents: MutableList<Observable<Event>> = mutableListOf()

    /**
     * Return the view model's [MviViewModel.uiState] if you want to be notified of the
     * UI state changes through the [onRenderUiState] function.
     * Return null if you are not interested in the UI state changes.
     */
    protected abstract fun getUiStateObservable(): Observable<UiState>?

    /**
     * Override this function to render the UI state. The function is called whenever the UI state changes.
     * It is also called after the configuration change (e.g. the device rotation) with the latest UI state.
     */
    protected open fun onRenderUiState(uiState: UiState) = Unit

    /**
     * Return the view model's [MviViewModel.uiEffects] if you want to be notified of the
     * UI effects through the [onRenderUiEffect] function.
     * Return null if you are not interested in the UI effects.
     */
    protected abstract fun getUiEffectObservable(): Observable<UiEffect>?

    /**
     * Override this function to render/process the UI effect.
     */
    protected open fun onRenderUiEffect(uiEffect: UiEffect) = Unit

    /**
     * Return the function that UI events will be forwarded to. Usually this will be
     * the view model's [MviViewModel.addEvent] function
     */
    protected abstract fun getUiEventsConsumer(): (Event) -> Unit

    /**
     * Add your UI events here (as Observables) using the [addUiEvent] function.
     */
    protected open fun onSetupUiEvents() = Unit

    /**
     * Add a UI event (as an Observable) that the view model will listen to.
     * Use RxBinding lib to convert UI callbacks to Observables.
     */
    protected fun addUiEvent(event: Observable<Event>) = uiEvents.add(event)

    /**
     * Subscribe to the view model's [MviViewModel.uiState] and [MviViewModel.uiEffects] and call the corresponding
     * render functions upon receiving a new UI state or a UI effect.
     */
    private fun subscribeToViewModel() {
        getUiStateObservable()?.apply {
            observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiState ->
                    try {
                        Timber.d("MVI-Rendering new Ui state: $uiState")
                        onRenderUiState(uiState)
                    } catch (exception: Throwable) {
                        Timber.wtf("MVI-Critical app error while rendering UI state:\n${exception.stackToString()}")
                    }
                }) { exception ->
                    Timber.wtf("MVI-Critical app error while precessing UI state:\n${exception.stackToString()}")
                }
                .addTo(disposables)
        }
        getUiEffectObservable()?.apply {
            observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiEffect ->
                    try {
                        Timber.d("MVI-Rendering new Ui effect: $uiEffect")
                        onRenderUiEffect(uiEffect)
                    } catch (exception: Throwable) {
                        Timber.wtf("MVI-Critical app error while rendering UI effect:\n${exception.stackToString()}")
                    }
                }) { exception ->
                    Timber.wtf("MVI-Critical app error while processing UI effect:\n${exception.stackToString()}")
                }
                .addTo(disposables)
        }
    }

    /**
     * Subscribes the UI events consumer to the UI events Observables.
     * E.g. when a button is clicked -> [MviViewModel.addEvent] is called -> the view model starts processing
     * the event.
     */
    private fun subscribeToUiEvents() {
        if (uiEvents.size > 0) {
            Observable.merge(uiEvents).subscribe(getUiEventsConsumer())
            { exception ->
                Timber.wtf("MVI-Critical app error while processing the user's input:\n${exception.stackToString()}")
            }
                .addTo(disposables)
        }
    }

    /**
     * Override if you want to handle the hardware BACK button tapped event.
     *
     * @return true if the fragment handles the event itself, false - the fragment has no special handling for this
     * key and thus, the tap must be forwarded to another fragment or the hosting activity.
     */
    open fun onBackPressed(): Boolean = false

    override fun onStart() {
        super.onStart()
        subscribeToViewModel()
        onSetupUiEvents()
        subscribeToUiEvents()
    }

    override fun onStop() {
        super.onStop()
        uiEvents.clear()
        disposables.clear()
    }
}