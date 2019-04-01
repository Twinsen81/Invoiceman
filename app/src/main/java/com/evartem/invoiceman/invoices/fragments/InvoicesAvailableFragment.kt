package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModel
import com.evartem.invoiceman.util.StatusDialog
import com.evartem.invoiceman.util.hideKeyboard
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.utils.DiffCallback
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil
import com.mikepenz.itemanimators.ScaleUpAnimator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_invoices_available.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

class InvoicesAvailableFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    companion object {
        const val INVOICE_ITEM_TYPE_BASIC = 1
    }

    private val viewModel by sharedViewModel<InvoicesViewModel>(from = { parentFragment!! })

    private lateinit var itemsAdapter: ItemAdapter<InvoiceItem>

    private var disposables: CompositeDisposable = CompositeDisposable()
    private val uiStates: PublishSubject<InvoicesUiState> = PublishSubject.create()

    private lateinit var statusDialog: StatusDialog

    init {
        Timber.d("Lifecycle: class instance crated")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("Lifecycle: onCreateView")
        return inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoices_available, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("Lifecycle: onActivityCreated")

        setupRecyclerView()
        setupRecyclerViewAsyncRenderingWithDiff()

        statusDialog = StatusDialog(context!!)

        setupUiEvents()

        subscribeToViewModel()
    }

    private fun setupRecyclerView() {

        itemsAdapter = ItemAdapter()
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_available_recyclerView.layoutManager = linearLayoutManager
        invoices_available_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter))

        invoices_available_recyclerView.itemAnimator = ScaleUpAnimator()

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
    }

    private fun setupRecyclerViewAsyncRenderingWithDiff() {
        uiStates
            .map { it.invoices }
            .flatMap { listOfInvoices ->
                Observable.fromIterable(listOfInvoices)
                    .map { item -> InvoiceItem(item) }
                    .toList()
                    .toObservable()
            }
            .map {
                FastAdapterDiffUtil.calculateDiff(itemsAdapter, it, object : DiffCallback<InvoiceItem?> {
                    override fun areItemsTheSame(oldItem: InvoiceItem?, newItem: InvoiceItem?) =
                        newItem?.invoice?.id == oldItem?.invoice?.id

                    override fun getChangePayload(
                        oldItem: InvoiceItem?,
                        oldItemPosition: Int,
                        newItem: InvoiceItem?,
                        newItemPosition: Int
                    ): Any? = null

                    override fun areContentsTheSame(oldItem: InvoiceItem?, newItem: InvoiceItem?) =
                        oldItem?.invoice == newItem?.invoice
                })
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FastAdapterDiffUtil.set(itemsAdapter, it)
                invoices_available_recyclerView.layoutManager?.scrollToPosition(0)
            }.addTo(disposables)
    }

    private fun setupUiEvents() {

        addUiEvent(swipeRefreshLayout.refreshes()
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
                .map { searchEvent -> InvoicesEvent.Search(searchEvent.queryText.toString()) })
    }

    override fun onBackPressed(): Boolean =
        when {
            searchView.isVisible -> {
                hideKeyboard(activity!!)
                viewModel.addEvent(InvoicesEvent.Search(stopSearch = true))
                true
            }
            else -> false
        }

    override fun onRenderUiState(uiState: InvoicesUiState) {

        // Render asynchronously with diffing
        uiStates.onNext(uiState)

        // region "Fetching invoices..." dialog
        if (uiState.isLoading)
            statusDialog.show(resources.getString(com.evartem.invoiceman.R.string.invoices_loading_text))
        else
            statusDialog.hide()
        // endregion

        // region "Nothing to process" text and image
        val showNothingToProcess =
            if (!uiState.isLoading && uiState.invoices.isEmpty()) View.VISIBLE else View.GONE
        invoices_available_status_text.visibility = showNothingToProcess
        invoices_available_status_image.visibility = showNothingToProcess
        // endregion

        // region SearchView text box
        searchView.visibility = if (uiState.searchViewOpen) View.VISIBLE else View.GONE
        searchView.setQuery(uiState.searchRequest, false)
        if (uiState.setFocusToSearchView) {
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
        }
        // endregion

        // region Refreshing: animation
        swipeRefreshLayout.isRefreshing = uiState.isRefreshing
        swipeRefreshLayout.isEnabled = !uiState.searchViewOpen
        // endregion

        // region Filter items if the search is active
        if (searchView.isVisible && uiState.searchRequest.isNotBlank())
            itemsAdapter.filter(uiState.searchRequest)
        else
            itemsAdapter.filter(null)
        // endregion
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = null

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent

    override fun onDestroyView() {
        // Clear the reference to the adapter to prevent leaking this layout
        invoices_available_recyclerView.adapter = null

        disposables.clear()
        super.onDestroyView()
    }
}