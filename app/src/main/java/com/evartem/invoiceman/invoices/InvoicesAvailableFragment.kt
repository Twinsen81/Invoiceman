package com.evartem.invoiceman.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.util.getRandomPeaksForGradientChart
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices_available.*

class InvoicesAvailableFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    private lateinit var viewModel: InvoicesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(parentFragment!!).get(InvoicesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_invoices_available, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupUiEvents()

        invoices_available_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupUiEvents() {

        addUiEvent(invoices_available_refreshButton.clicks().map { InvoicesEvent.RefreshScreenEvent })

        addUiEvent(
            invoices_available_searchButton.clicks()
                .map { invoices_available_searchText.text.trim() }
                .filter { text -> text.isNotBlank() }
                .map { InvoicesEvent.SearchInvoiceEvent(it.toString()) })
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {
        invoices_available_text.text = "INVOICES: ${uiState.searchRequest}, ${uiState.invoices.size}"
        invoices_available_loading.visibility = if (uiState.loadingIndicator) View.VISIBLE else View.GONE
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent
}