package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.backendsim.RandomInvoiceGenerator
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.InvoicesViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.util.getRandomPeaksForGradientChart
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices_available.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InvoicesAvailableFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    companion object {
        const val INVOICE_ITEM_TYPE_BASIC = 1
    }

    private val viewModel by sharedViewModel<InvoicesViewModel>(from = {parentFragment!!})

    private val itemsAdapter = ItemAdapter<InvoiceItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoices_available, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupUiEvents()

        invoices_available_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupRecyclerView() {
        invoices_available_recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_available_recyclerView.adapter = FastAdapter.with<InvoiceItem, ItemAdapter<InvoiceItem>>(itemsAdapter)
        itemsAdapter.set(RandomInvoiceGenerator.getInvoices(5).map { InvoiceItem(it) })
    }

    private fun setupUiEvents() {

/*        addUiEvent(invoices_available_refreshButton.clicks().map { InvoicesEvent.RefreshScreenEvent })

        addUiEvent(
            invoices_available_searchButton.clicks()
                .map { invoices_available_searchText.text.trim() }
                .filter { text -> text.isNotBlank() }
                .map { InvoicesEvent.SearchInvoiceEvent(it.toString()) })*/
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent
}