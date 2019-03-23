package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
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

class InvoicesAvailableFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    companion object {
        const val INVOICE_ITEM_TYPE_BASIC = 1
    }

    private lateinit var viewModel: InvoicesViewModel
    private val itemsAdapter = ItemAdapter<InvoiceItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(parentFragment!!).get(InvoicesViewModel::class.java)
        return inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoices_available, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupUiEvents()

        invoices_available_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupRecyclerView() {
        invoices_available_recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_available_recyclerView.adapter = FastAdapter.with<InvoiceItem, ItemAdapter<InvoiceItem>>(itemsAdapter)
        itemsAdapter.set(RandomInvoiceGenerator.getInvoices(3).map { InvoiceItem(it) })

        // TODO: Just a bordered layout instead of CardView:
        // TODO: https://stackoverflow.com/questions/8203606/android-how-to-draw-a-border-to-a-linearlayout
        // TODO: Fix scrolling issue with the nav app bar
        // TODO: The item's layout - nicer (+ smaller font for the products)
        // https://www.photohdx.com/images/2017/04/clean-white-paper-background-texture.jpg
        // TODO: performance! - opens too slowly! Random?
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