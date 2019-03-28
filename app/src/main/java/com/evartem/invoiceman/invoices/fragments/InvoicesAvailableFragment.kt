package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.util.InvisibleItem
import com.evartem.invoiceman.util.StatusDialog
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices_available.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class InvoicesAvailableFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    companion object {
        const val INVOICE_ITEM_TYPE_BASIC = 1
        const val INVOICE_ITEM_TYPE_INVISIBLE = 2
    }

    private val viewModel by sharedViewModel<InvoicesViewModel>(from = { parentFragment!! })

    private lateinit var itemsAdapter: ItemAdapter<InvoiceItem>
    private lateinit var itemsAdapterInvisibleFooter: ItemAdapter<InvisibleItem>

    private lateinit var statusDialog: StatusDialog

    private lateinit var previousUiState: InvoicesUiState

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoices_available, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        previousUiState = InvoicesUiState()

        setupRecyclerView()

        statusDialog = StatusDialog(context!!)

        setupUiEvents()

        subscribeToViewModel()

//        invoices_available_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupRecyclerView() {
        itemsAdapter = ItemAdapter()
        itemsAdapterInvisibleFooter = ItemAdapter()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_available_recyclerView.layoutManager = linearLayoutManager
        invoices_available_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter, itemsAdapterInvisibleFooter))
        itemsAdapterInvisibleFooter.set(listOf(InvisibleItem()))

        // Fix a problem when user scrolls up and upon reaching the top of the list the Refresh event is triggered
        invoices_available_recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                swipeRefreshLayout.isEnabled = swipeRefreshLayout.isRefreshing || linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
    }

    private fun setupUiEvents() {

        addUiEvent(swipeRefreshLayout.refreshes().map { InvoicesEvent.RefreshScreenEvent })
/*    addUiEvent(invoices_available_refreshButton.clicks().map { InvoicesEvent.RefreshScreenEvent })

        addUiEvent(
            invoices_available_searchButton.clicks()
                .map { invoices_available_searchText.text.trim() }
                .filter { text -> text.isNotBlank() }
                .map { InvoicesEvent.SearchInvoiceEvent(it.toString()) })*/
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {

        if (uiState.invoices.isNotEmpty() && uiState.invoices != previousUiState.invoices) {
            itemsAdapter.clear()
            invoices_available_recyclerView.layoutManager?.scrollToPosition(0)
            itemsAdapter.add(uiState.invoices.map { InvoiceItem(it) })
        } else if (uiState.invoices.isEmpty())
            itemsAdapter.set(emptyList())

        if (uiState.isLoading)
            statusDialog.show(resources.getString(R.string.invoices_loading_text))
        else
            statusDialog.hide()

        val showNothingToProcessMessage =
            if (!uiState.isLoading && uiState.invoices.isEmpty()) View.VISIBLE else View.GONE
        invoices_available_status_text.visibility = showNothingToProcessMessage
        invoices_available_status_image.visibility = showNothingToProcessMessage

        swipeRefreshLayout.isRefreshing = uiState.isRefreshing

        previousUiState = uiState.copy()
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent

    override fun onDestroy() {
        statusDialog.hide()
        super.onDestroy()
    }
}