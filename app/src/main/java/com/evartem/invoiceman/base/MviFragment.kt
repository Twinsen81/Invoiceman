package com.evartem.invoiceman.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

abstract class MviFragment<UiState, UiEffect, UiEvent> : Fragment() {

    private var viewModelDisposables: CompositeDisposable = CompositeDisposable()
    private var uiDisposable: Disposable? = null
    private val uiEvents: MutableList<Observable<UiEvent>> = mutableListOf()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribeToViewModel()
    }

    protected abstract fun getUiStateObservable(): Observable<UiState>?

    protected abstract fun renderUiState(uiState: UiState)

    protected abstract fun getUiEffectObservable(): Observable<UiEffect>?

    protected abstract fun renderUiEffect(uiEffect: UiEffect)

    private fun subscribeToViewModel() {
        getUiStateObservable()?.apply {
            doOnNext { Timber.d("MVI-New state: $it") }
                .subscribe(::renderUiState) { Timber.wtf("MVI-Critical app error while precessing UI state") }
                .addTo(viewModelDisposables)
        }
        getUiEffectObservable()?.apply {
            doOnNext { Timber.d("MVI-New effect: $it") }
                .subscribe(::renderUiEffect) { Timber.wtf("MVI-Critical app error while processing UI effect") }
                .addTo(viewModelDisposables)
        }
    }

    protected fun addUiEvent(uiEvent: Observable<UiEvent>) = uiEvents.add(uiEvent)

    protected abstract fun getUiEventsConsumer(): (UiEvent) -> Unit

    private fun subscribeToUiEvents() {
        uiDisposable = Observable.merge(uiEvents).subscribe(getUiEventsConsumer())
        { Timber.wtf("MVI-Critical app error while processing the user's input") }
    }

    override fun onResume() {
        super.onResume()
        subscribeToUiEvents()
    }

    override fun onPause() {
        super.onPause()
        uiDisposable?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelDisposables.clear()
    }
}