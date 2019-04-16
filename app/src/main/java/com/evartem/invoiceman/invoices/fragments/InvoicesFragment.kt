package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.domain.entity.doc.Invoice
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModel
import com.evartem.invoiceman.util.SessionManager
import com.evartem.invoiceman.util.StatusDialog
import com.evartem.invoiceman.util.hideKeyboard
import com.evartem.invoiceman.util.itemClicks
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
import kotlinx.android.synthetic.main.fragment_invoices.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class InvoicesFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    companion object {
        // A FastAdapter item type
        const val INVOICE_ITEM_TYPE_BASIC = 1
    }

    /**
     * The options define the fragment's behaviour. Passed by the parent through [setArguments].
     */
    private lateinit var fragmentOptions: InvoicesFragmentOptions

    private val viewModel by sharedViewModel<InvoicesViewModel>(from = { parentFragment!! })
    private val sessionManager: SessionManager by inject()

    private lateinit var itemsAdapter: ItemAdapter<InvoiceItem>

    private var disposables: CompositeDisposable = CompositeDisposable()

    /**
     * A subject to diff and render the recycler view asynchronously.
     */
    private val listOfInvoices: PublishSubject<List<Invoice>> = PublishSubject.create()

    /**
     * By default, upon resume the fragment just renders the last UI state. If this property is true,
     * then the data is also refreshed upon resume (since the data might have changed by now).
     */
    private var reloadDataOnResume: Boolean = false

    private lateinit var statusDialog: StatusDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentOptions = InvoicesFragmentOptions.fromBundle(arguments!!)

        savedInstanceState?.let {
            reloadDataOnResume = it.getBoolean("reloadDataOnResume", false)
        }
        return inflater.inflate(R.layout.fragment_invoices, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupRecyclerViewAsyncRenderingWithDiff()

        statusDialog = StatusDialog(context!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("reloadDataOnResume", reloadDataOnResume)
    }

    override fun onResume() {
        super.onResume()

        // The user has come back from the invoiceDetail fragment where he possibly changed
        // the state of invoices (accepted, returned or submitted) - hence, reload data
        // from the local data source to reflect those changes
        if (fragmentOptions.reloadDataOnResume && reloadDataOnResume) {
            reloadDataOnResume = false
            viewModel.addEvent(InvoicesEvent.LoadScreen(false))
            return
        }
    }

    private fun setupRecyclerView() {

        itemsAdapter = ItemAdapter()

        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        invoices_recyclerView.layoutManager = linearLayoutManager
        invoices_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter))

        invoices_recyclerView.itemAnimator = ScaleUpAnimator()

        // Setup search conditions for the recycler view (adapter)
        itemsAdapter.itemFilter.withFilterPredicate { item, constraint ->
            item.invoice.seller.contains(constraint ?: "", true) ||
                    item.invoice.number.toString().contains(constraint ?: "", true) ||
                    item.invoice.date.contains(constraint ?: "", true)
        }

        // Fix a problem when user scrolls up and upon reaching the top of the list the Refresh event is triggered
        invoices_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                swipeRefreshLayout.isEnabled = !searchView.isVisible && (swipeRefreshLayout.isRefreshing ||
                        linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
            }
        })
    }

    private fun setupRecyclerViewAsyncRenderingWithDiff() {
        listOfInvoices
            .subscribeOn(Schedulers.io())
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FastAdapterDiffUtil.set(itemsAdapter, it)
                invoices_recyclerView.layoutManager?.scrollToPosition(0)
            }.addTo(disposables)
    }

    override fun onSetupUiEvents() {

        addUiEvent(itemsAdapter.fastAdapter.itemClicks()
            .throttleFirst(1, TimeUnit.SECONDS)
            .map { invoiceItem -> InvoicesEvent.Click(invoiceItem.invoice.id) })

        addUiEvent(swipeRefreshLayout.refreshes()
            .doOnNext { if (searchView.isVisible) swipeRefreshLayout.isRefreshing = false }
            .map { InvoicesEvent.RefreshScreen })

        addUiEvent(
            searchView.findViewById<ImageView>(R.id.search_close_btn).clicks()
                .doOnNext { hideKeyboard(activity!!) }
                .map { InvoicesEvent.Search(stopSearch = true) })

        addUiEvent(
            searchView.queryTextChangeEvents()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { searchEvent -> searchEvent.isSubmitted && searchEvent.queryText.isNotBlank() }
                .map { searchEvent -> InvoicesEvent.Search(searchEvent.queryText.toString()) })
    }

    override fun onBackPressed(): Boolean {
        if (lifecycle.currentState != Lifecycle.State.RESUMED) return false
        return when {
            searchView.isVisible -> {
                hideKeyboard(activity!!)
                viewModel.addEvent(InvoicesEvent.Search(stopSearch = true))
                true
            }
            else -> false
        }
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {

        // Render the recycler view asynchronously with diffing
        if (uiState.invoices.isNotEmpty())
            listOfInvoices.onNext(uiState.invoices.filter {
                if (fragmentOptions.filterTypeInProgress)
                    it.processedByUser == sessionManager.currentUser.id
                else
                    it.processedByUser.isNullOrEmpty()
            })
        else // Diffing crashes when updating from an non-empty list -> empty
            itemsAdapter.clear()

        // region "Fetching invoices..." dialog
        if (uiState.isLoading)
            statusDialog.show(resources.getString(R.string.invoices_loading_text))
        else
            statusDialog.hide()
        // endregion

        // region "Nothing to process" text and image
        val showNothingToProcess =
            if (!uiState.isLoading && uiState.invoices.isEmpty()) View.VISIBLE else View.GONE
        invoices_status_text.visibility = showNothingToProcess
        invoices_status_image.visibility = showNothingToProcess
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

    override fun onRenderUiEffect(uiEffect: InvoicesUiEffect) {
        if (fragmentOptions.navigateToDetailOnClick && uiEffect is InvoicesUiEffect.InvoiceClick) {
            reloadDataOnResume = true
            sessionManager.setInvoice(uiEffect.invoice)
            findNavController().navigate(R.id.action_destination_invoiceList_to_destination_invoiceDetail)
        }
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent

    override fun onDestroyView() {
        // Clear the reference to the adapter to prevent leaking this layout
        invoices_recyclerView.adapter = null
        disposables.clear()
        super.onDestroyView()
    }
}