package com.evartem.invoiceman.base

import androidx.fragment.app.Fragment
import com.evartem.invoiceman.util.stackToString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

abstract class MviFragment<UiState, UiEffect, Event> : Fragment() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    private val uiEvents: MutableList<Observable<Event>> = mutableListOf()

    protected abstract fun getUiStateObservable(): Observable<UiState>?

    protected open fun onRenderUiState(uiState: UiState) = Unit

    protected abstract fun getUiEffectObservable(): Observable<UiEffect>?

    protected open fun onRenderUiEffect(uiEffect: UiEffect) = Unit

    protected abstract fun getUiEventsConsumer(): (Event) -> Unit

    protected abstract fun onSetupUiEvents()

    protected fun addUiEvent(event: Observable<Event>) = uiEvents.add(event)

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
                    Timber.wtf("MVI-Critical app error while precessing UI state:\n${exception.stackToString()}") }
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

    private fun subscribeToUiEvents() {
        if (uiEvents.size > 0) {
            Observable.merge(uiEvents).subscribe(getUiEventsConsumer())
            { exception ->
                Timber.wtf("MVI-Critical app error while processing the user's input:\n${exception.stackToString()}")
            }
                .addTo(disposables)
        }
    }

    open fun onBackPressed(): Boolean = false

    override fun onResume() {
        super.onResume()
        subscribeToViewModel()
        onSetupUiEvents()
        subscribeToUiEvents()
    }

    override fun onPause() {
        super.onPause()
        uiEvents.clear()
        disposables.clear()
    }
}

