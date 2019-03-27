package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.InvoicesViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.util.getRandomPeaksForGradientChart
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices_inprogress.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class InvoicesInProgressFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    private val viewModel by sharedViewModel<InvoicesViewModel>(from = { parentFragment!! })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_invoices_inprogress, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupUiEvents()

        subscribeToViewModel()

//        invoices_in_progress_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupUiEvents() {

        Timber.d("MVI ($viewModel): setupUiEvents")
        addUiEvent(invoices_in_progress_refreshButton.clicks().map {
            Timber.d("MVI ($viewModel): refresh click")
            InvoicesEvent.LoadScreenEvent
        })

        addUiEvent(
            invoices_in_progress_searchButton.clicks()
                .map { invoices_in_progress_searchText.text.trim() }
                .filter { text -> text.isNotBlank() }
                .map { InvoicesEvent.SearchInvoiceEvent(it.toString()) })
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {
        invoices_in_progress_text.text = "INVOICES: ${uiState.searchRequest}, ${uiState.invoices.size}"
        invoices_in_progress_loading.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent
}