package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import com.jakewharton.rxbinding3.appcompat.SearchViewQueryTextEvent
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices_available.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

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
        invoices_available_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                swipeRefreshLayout.isEnabled = !previousUiState.searchViewOpen &&
                    swipeRefreshLayout.isRefreshing || linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
    }

    private fun setupUiEvents() {

        addUiEvent(swipeRefreshLayout.refreshes().map { InvoicesEvent.RefreshScreen })

        addUiEvent(
            searchView.queryTextChangeEvents()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { searchEvent -> searchEvent.isSubmitted && searchEvent.queryText.isNotBlank() }
                .map { searchEvent -> InvoicesEvent.Search(searchEvent.queryText.toString()) })
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {

        if (uiState.invoices.isNotEmpty() && (uiState.isInvoicesChanged || previousUiState.invoices.isEmpty())) {
            itemsAdapter.clear()
            invoices_available_recyclerView.layoutManager?.scrollToPosition(0)
            itemsAdapter.add(uiState.invoices.map { InvoiceItem(it) })
        } else if (uiState.invoices.isEmpty())
            itemsAdapter.set(emptyList())

        if (uiState.isLoading)
            statusDialog.show(resources.getString(R.string.invoices_loading_text))
        else
            statusDialog.hide()

        val showNothingToProcess =
            if (!uiState.isLoading && uiState.invoices.isEmpty()) View.VISIBLE else View.GONE
        invoices_available_status_text.visibility = showNothingToProcess
        invoices_available_status_image.visibility = showNothingToProcess

        swipeRefreshLayout.isRefreshing = uiState.isRefreshing

        searchView.visibility = if (uiState.searchViewOpen) View.VISIBLE else View.GONE
        searchView.setQuery(uiState.searchRequest, false)
        if (searchView.isVisible)
        {
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
        }

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