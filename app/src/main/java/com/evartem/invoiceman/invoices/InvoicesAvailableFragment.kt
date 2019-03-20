package com.evartem.invoiceman.invoices

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.evartem.invoiceman.R
import com.evartem.invoiceman.main.BaseFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_invoices_available.*
import timber.log.Timber

class InvoicesAvailableFragment : BaseFragment() {

    private lateinit var viewModel: InvoicesViewModel

    private var viewModelDisposables: CompositeDisposable = CompositeDisposable()
    private var uiDisposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_invoices_available, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(InvoicesViewModel::class.java)
        subscribeToViewModel()

        invoices_available_gradientChart.chartValues = randomGradientChart()
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) = Unit

    private fun render(uiState: InvoicesUiState) {
        invoices_available_text.text = "INVOICES: ${uiState.searchRequest}, ${uiState.invoices.size}"
        invoices_available_loading.visibility = if (uiState.loadingIndicator) View.VISIBLE else View.GONE
    }

    private fun renderEffect(uiEffect: InvoicesUiEffect) {
        if (uiEffect is InvoicesUiEffect.NetworkError)
            Toast.makeText(context, uiEffect.message, Toast.LENGTH_LONG).show()
    }

    private fun subscribeToViewModel() {
        viewModel.uiState
            .doOnNext { Timber.d("New state: $it") }
            .subscribe(::render) { Timber.wtf("Critical app error while precessing UI state") }
            .addTo(viewModelDisposables)

        viewModel.uiEffects
            .doOnNext { Timber.d("New effect: $it") }
            .subscribe(::renderEffect) { Timber.wtf("Critical app error while preocessing UI effect") }
            .addTo(viewModelDisposables)
    }

    private fun subscribeToUiEvents() {

        val refreshScreenEvent = RxView.clicks
        object RefreshScreenEvent : InvoicesEvent()
        data class SearchInvoiceEvent(val searchQuery: String) : InvoicesEvent()

    }

    override fun onPause() {
        super.onPause()
        uiDisposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        subscribeToUiEvents()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelDisposables.clear()
    }
}