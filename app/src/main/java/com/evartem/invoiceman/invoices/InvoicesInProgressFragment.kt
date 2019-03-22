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
import kotlinx.android.synthetic.main.fragment_invoices_inprogress.*

class InvoicesInProgressFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    private lateinit var viewModel: InvoicesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(parentFragment!!).get(InvoicesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_invoices_inprogress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupUiEvents()

        invoices_in_progress_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupUiEvents() {

        addUiEvent(invoices_in_progress_refreshButton.clicks().map { InvoicesEvent.RefreshScreenEvent })

        addUiEvent(
            invoices_in_progress_searchButton.clicks()
                .map { invoices_in_progress_searchText.text.trim() }
                .filter { text -> text.isNotBlank() }
                .map { InvoicesEvent.SearchInvoiceEvent(it.toString()) })
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {
        invoices_in_progress_text.text = "INVOICES: ${uiState.searchRequest}, ${uiState.invoices.size}"
        invoices_in_progress_loading.visibility = if (uiState.loadingIndicator) View.VISIBLE else View.GONE
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent
}