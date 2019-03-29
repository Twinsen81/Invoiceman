package com.evartem.invoiceman.invoice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailEvent
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailUiEffect
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailUiState
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailViewModel
import com.evartem.invoiceman.util.StatusDialog
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoice_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvoiceDetailFragment : MviFragment<InvoiceDetailUiState, InvoiceDetailUiEffect, InvoiceDetailEvent>() {

    companion object {
        const val INVOICE_ITEM_TYPE_BASIC = 1
        const val INVOICE_ITEM_TYPE_INVISIBLE = 2
    }

    private val viewModel by viewModel<InvoiceDetailViewModel>()

    private lateinit var statusDialog: StatusDialog

    private lateinit var previousUiState: InvoiceDetailUiState

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoice_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        previousUiState = InvoiceDetailUiState.EmptyUiState

        //setupRecyclerView()

        statusDialog = StatusDialog(context!!)

        setupUiEvents()

        subscribeToViewModel()
    }

/*    private fun setupRecyclerView() {

        itemsAdapter = ItemAdapter()
        itemsAdapterInvisibleFooter = ItemAdapter()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_available_recyclerView.layoutManager = linearLayoutManager
        invoices_available_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter, itemsAdapterInvisibleFooter))
        itemsAdapterInvisibleFooter.set(listOf(InvisibleItem()))

        itemsAdapter.itemFilter.withFilterPredicate { item, constraint ->
            item.invoice.seller.contains(constraint ?: "", true) ||
                    item.invoice.number.toString().contains(constraint ?: "", true) ||
                    item.invoice.date.contains(constraint ?: "", true)
        }

        // Fix a problem when user scrolls up and upon reaching the top of the list the Refresh event is triggered
        invoices_available_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                swipeRefreshLayout.isEnabled = !searchView.isVisible && swipeRefreshLayout.isRefreshing ||
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
    }*/

    private fun setupUiEvents() {

   /*     addUiEvent(swipeRefreshLayout.refreshes()
            .doOnNext { if (searchView.isVisible) swipeRefreshLayout.isRefreshing = false }
            .map { InvoicesEvent.RefreshScreen })

        addUiEvent(
            searchView.findViewById<ImageView>(com.evartem.invoiceman.R.id.search_close_btn).clicks()
                .doOnNext { hideKeyboard(activity!!) }
                .map { InvoicesEvent.Search(stopSearch = true) })

        addUiEvent(
            searchView.queryTextChangeEvents()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { searchEvent -> searchEvent.isSubmitted && searchEvent.queryText.isNotBlank() }
                .map { searchEvent -> InvoicesEvent.Search(searchEvent.queryText.toString()) })*/
    }

/*    override fun onBackPressed(): Boolean =
        when {
            searchView.isVisible -> {
                hideKeyboard(activity!!)
                viewModel.addEvent(InvoicesEvent.Search(stopSearch = true))
                true
            }
            else -> false
        }*/

    override fun onRenderUiState(uiState: InvoiceDetailUiState) {

        invoice_seller.text = uiState.invoice.seller
        invoice_number.text = uiState.invoice.number.toString()
        invoice_date.text = uiState.invoice.date

        if (uiState.invoice.comment.isNullOrEmpty()) {
            invoice_comment.visibility = View.VISIBLE
            invoice_comment.text = uiState.invoice.comment
            invoice_label_comment.visibility = View.VISIBLE
        } else {
            invoice_comment.visibility = View.GONE
            invoice_label_comment.visibility = View.GONE
        }

        // Memorize current state
        previousUiState = uiState.copy()
    }

    override fun getUiStateObservable(): Observable<InvoiceDetailUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoiceDetailUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoiceDetailEvent) -> Unit = viewModel::addEvent

/*
    override fun onDestroyView() {
        // Clear the reference to the adapter to prevent leaking this layout
        TODO() //invoices_available_recyclerView.adapter = null
        super.onDestroyView()
    }
*/

}