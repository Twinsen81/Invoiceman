package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.InvoicesViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.util.LoadingDialog
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

    private val viewModel by sharedViewModel<InvoicesViewModel>(from = { parentFragment!! })

    private lateinit var itemsAdapter: ItemAdapter<InvoiceItem>

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoices_available, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()

        loadingDialog = LoadingDialog(context!!)

        setupUiEvents()

        subscribeToViewModel()

        invoices_available_gradientChart.chartValues = getRandomPeaksForGradientChart()
    }

    private fun setupRecyclerView() {
        itemsAdapter = ItemAdapter()
        invoices_available_recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_available_recyclerView.adapter = FastAdapter.with<InvoiceItem, ItemAdapter<InvoiceItem>>(itemsAdapter)
    }

    private fun setupUiEvents() {

/*    addUiEvent(invoices_available_refreshButton.clicks().map { InvoicesEvent.RefreshScreenEvent })

        addUiEvent(
            invoices_available_searchButton.clicks()
                .map { invoices_available_searchText.text.trim() }
                .filter { text -> text.isNotBlank() }
                .map { InvoicesEvent.SearchInvoiceEvent(it.toString()) })*/
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {
        if (uiState.invoices.isNotEmpty())
            itemsAdapter.set(uiState.invoices.map { InvoiceItem(it) })
        else
            itemsAdapter.set(emptyList())

        if (uiState.loadingIndicator)
            loadingDialog.show(resources.getString(R.string.invoices_loading_text))
        else
            loadingDialog.hide()

        val showNothingToProcess =
            if (!uiState.loadingIndicator && uiState.invoices.isEmpty()) View.VISIBLE else View.GONE
        invoices_available_status_text.visibility = showNothingToProcess
        invoices_available_status_image.visibility = showNothingToProcess
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent

    override fun onDestroy() {
        loadingDialog.hide()
        super.onDestroy()
    }
}