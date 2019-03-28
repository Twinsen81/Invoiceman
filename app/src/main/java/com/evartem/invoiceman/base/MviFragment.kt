package com.evartem.invoiceman.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.evartem.invoiceman.R
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.evartem.invoiceman.navigation.MainActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_invoices.*
import timber.log.Timber

abstract class MviFragment<UiState, UiEffect, Event> : Fragment() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    private val uiEvents: MutableList<Observable<Event>> = mutableListOf()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupBottomAppBarAndFAB()
    }

    private fun setupBottomAppBarAndFAB() {
        val bottomAppBar: BottomAppBar? = view?.findViewById(R.id.bottomAppBar)
        val fab: FloatingActionButton? = view?.findViewById(R.id.fab)
        if (bottomAppBar != null && fab != null) {
            setupBottomAppBarNavigation()
            onConfigureBottomAppBar(bottomAppBar, fab)
            fab.setOnClickListener { onFABClicked() }
        }
    }

    protected open fun onFABClicked() {
        Navigation.findNavController((activity as MainActivity).navigation_host.view!!).popBackStack()
    }

    private fun setupBottomAppBarNavigation() {
        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }

    protected open fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        fab.hide()
        bottomAppBar.visibility = View.GONE
    }

    protected abstract fun getUiStateObservable(): Observable<UiState>?

    protected open fun onRenderUiState(uiState: UiState) = Unit

    protected abstract fun getUiEffectObservable(): Observable<UiEffect>?

    protected open fun onRenderUiEffect(uiEffect: UiEffect) = Unit

    protected fun subscribeToViewModel() {
        getUiStateObservable()?.apply {
            observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiState ->
                    try {
                        Timber.d("MVI-Rendering new Ui state: $uiState")
                        onRenderUiState(uiState)
                    } catch (ex: Throwable) {
                        Timber.wtf("MVI-Critical app error while rendering UI state:\n${Log.getStackTraceString(ex)}")
                    }
                }) { Timber.wtf("MVI-Critical app error while precessing UI state:\n${Log.getStackTraceString(it)}") }
                .addTo(disposables)
        }
        getUiEffectObservable()?.apply {
            observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiEffect ->
                    try {
                        Timber.d("MVI-Rendering new Ui effect: $uiEffect")
                        onRenderUiEffect(uiEffect)
                    } catch (ex: Throwable) {
                        Timber.wtf("MVI-Critical app error while rendering UI effect:\n${Log.getStackTraceString(ex)}")
                    }
                }) { Timber.wtf("MVI-Critical app error while processing UI effect:\n${Log.getStackTraceString(it)}") }
                .addTo(disposables)
        }
    }

    protected fun addUiEvent(event: Observable<Event>) = uiEvents.add(event)

    protected abstract fun getUiEventsConsumer(): (Event) -> Unit

    private fun subscribeToUiEvents() {
        if (uiEvents.size > 0) {
            Observable.merge(uiEvents).subscribe(getUiEventsConsumer())
            { Timber.wtf("MVI-Critical app error while processing the user's input:\n${Log.getStackTraceString(it)}") }
                .addTo(disposables)
        }
    }

    override fun onResume() {
        super.onResume()
        subscribeToUiEvents()
    }

    override fun onPause() {
        super.onPause()
        uiEvents.clear()
        disposables.clear()
    }
}

