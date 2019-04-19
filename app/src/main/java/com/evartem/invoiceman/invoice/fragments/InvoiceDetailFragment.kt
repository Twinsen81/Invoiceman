package com.evartem.invoiceman.invoice.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.domain.entity.doc.Product
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailEvent
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailUiEffect
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailUiState
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailViewModel
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.evartem.invoiceman.util.*
import com.jakewharton.rxbinding3.appcompat.itemClicks
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
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
import kotlinx.android.synthetic.main.fragment_invoice_detail.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

class InvoiceDetailFragment : MviFragment<InvoiceDetailUiState, InvoiceDetailUiEffect, InvoiceDetailEvent>() {

    companion object {
        const val PRODUCT_ITEM_TYPE_BASIC = 1
        const val FRAGMENT_STATE_RELOAD_DATA_ON_RESUME = "reloadDataOnResume"
        lateinit var processingStatusBackground: ProcessingStatusBackground
    }

    private val viewModel by viewModel<InvoiceDetailViewModel>()
    private val sessionManager: SessionManager by inject()

    private lateinit var itemsAdapter: ItemAdapter<ProductItem>

    private var disposables: CompositeDisposable = CompositeDisposable()

    // A subject to diff and render the recycler view asynchronously
    private val productsObservable: PublishSubject<List<Product>> = PublishSubject.create()

    // By default, upon resume the fragment just renders the last UI state. If this property is true,
    // then the data is also refreshed upon resume (since the data might have changed by now).
    private var reloadDataOnResume: Boolean = false

    private lateinit var statusDialog: StatusDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusDialog = StatusDialog(context!!)

        processingStatusBackground = ProcessingStatusBackground(context!!)

        savedInstanceState?.let {
            reloadDataOnResume = it.getBoolean(FRAGMENT_STATE_RELOAD_DATA_ON_RESUME, false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_invoice_detail, container, false)

    // KTX synthetic will work only from here (not in onCreateView)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        configureBottomAppBar()
    }

    private fun setupRecyclerView() {

        itemsAdapter = ItemAdapter()

        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        products_recyclerView.layoutManager = linearLayoutManager
        products_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter))

        products_recyclerView.itemAnimator = ScaleUpAnimator()

        // Setup search conditions for the recycler view (adapter)
        itemsAdapter.itemFilter.withFilterPredicate { item, constraint ->
            item.product.article.contains(constraint ?: "", true) ||
                    item.product.description.contains(constraint ?: "", true)
        }
    }

    private fun configureBottomAppBar() {
        bottomAppBar.navigationIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.replaceMenu(R.menu.invoice_detail)

        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }

    override fun getUiStateObservable(): Observable<InvoiceDetailUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoiceDetailUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (InvoiceDetailEvent) -> Unit = viewModel::addEvent

    override fun onStart() {
        super.onStart()

        setupRecyclerViewAsyncRenderingWithDiff()

        // If the user has come back from the ProductDetail fragment where he possibly changed
        // the state of products (add/edit/delete results) - reload data
        // from the local data source to reflect those changes
        if (reloadDataOnResume) {
            reloadDataOnResume = false
            viewModel.addEvent(InvoiceDetailEvent.LoadScreen)
            return
        }
    }

    private fun setupRecyclerViewAsyncRenderingWithDiff() {
        productsObservable
            .subscribeOn(Schedulers.io())
            .flatMap { listOfProducts ->
                Observable.fromIterable(listOfProducts)
                    .map { item -> ProductItem(item) }
                    .toList()
                    .toObservable()
            }
            .map {
                FastAdapterDiffUtil.calculateDiff(itemsAdapter, it, object : DiffCallback<ProductItem?> {
                    override fun areItemsTheSame(oldItem: ProductItem?, newItem: ProductItem?) =
                        newItem?.product?.id == oldItem?.product?.id

                    override fun getChangePayload(
                        oldItem: ProductItem?,
                        oldItemPosition: Int,
                        newItem: ProductItem?,
                        newItemPosition: Int
                    ): Any? = null

                    override fun areContentsTheSame(oldItem: ProductItem?, newItem: ProductItem?) =
                        oldItem?.product == newItem?.product
                })
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FastAdapterDiffUtil.set(itemsAdapter, it)
                products_recyclerView.layoutManager?.scrollToPosition(0)
            }.addTo(disposables)
    }

    override fun onSetupUiEvents() {
        addUiEvent(itemsAdapter.fastAdapter.itemClicks()
            .throttleFirst(1, TimeUnit.SECONDS)
            .map { ProductItem -> InvoiceDetailEvent.Click(ProductItem.product.id) })

        addUiEvent(
            searchView.findViewById<ImageView>(R.id.search_close_btn).clicks()
                .doOnNext { hideKeyboard(activity!!) }
                .map { InvoiceDetailEvent.Search(stopSearch = true) })

        addUiEvent(
            searchView.queryTextChangeEvents()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { searchEvent -> searchEvent.isSubmitted && searchEvent.queryText.isNotBlank() }
                .map { searchEvent -> InvoiceDetailEvent.Search(searchEvent.queryText.toString()) })

        addUiEvent(
            bottomAppBar.itemClicks()
                .throttleFirst(1, TimeUnit.SECONDS)
                .map { item ->
                    when (item.itemId) {
                        R.id.products_search -> InvoiceDetailEvent.Search(startSearch = true)
                        else -> InvoiceDetailEvent.Empty
                    }
                }.filter { it !is InvoiceDetailEvent.Empty })

        addUiEvent(
            invoice_action_accept.clicks()
                .throttleFirst(1, TimeUnit.SECONDS)
                .map { InvoiceDetailEvent.Accept })

        addUiEvent(
            invoice_action_return.clicks()
                .throttleFirst(1, TimeUnit.SECONDS)
                .map { InvoiceDetailEvent.Return })
    }

    // Hide the searchView and the keyboard on the Back tap
    override fun onBackPressed(): Boolean {
        if (lifecycle.currentState != Lifecycle.State.RESUMED) return false
        return when {
            searchView.isVisible -> {
                hideKeyboard(activity!!)
                viewModel.addEvent(InvoiceDetailEvent.Search(stopSearch = true))
                true
            }
            else -> false
        }
    }

    override fun onRenderUiState(uiState: InvoiceDetailUiState) {

        // Render the recycler view asynchronously with diffing
        if (uiState.invoice.products.isNotEmpty())
            productsObservable.onNext(uiState.invoice.products)
        else // Diffing crashes when updating from an non-empty list -> empty
            itemsAdapter.clear()

        // region Dialogs
        when {
            uiState.isRequestingAccept -> statusDialog.show(
                resources.getString(R.string.invoice_accepting)
            )
            uiState.isRequestingReturn -> statusDialog.show(
                resources.getString(R.string.invoice_returning)
            )
            uiState.isSubmitting -> statusDialog.show(
                resources.getString(R.string.invoice_submitting)
            )
            else -> statusDialog.hide()
        }
        // endregion

        invoice_seller.text = uiState.invoice.seller
        invoice_number.text = uiState.invoice.number.toString()
        invoice_date.text = uiState.invoice.date

        invoice_info_panel.setBackgroundColor(
            when {
                uiState.invoice.isProcessingFinishedSuccessfully ->
                    processingStatusBackground.finishedWithoutErrors
                uiState.invoice.isProcessingFinishedWithErrors ->
                    processingStatusBackground.finishedWithErrors
                else -> processingStatusBackground.notEvenStarted
            }
        )

        if (uiState.isBeingProcessedByUser) {
            invoice_action_accept.visibility = View.GONE
            invoice_action_return.visibility = View.VISIBLE
            invoice_action_submit.visibility = View.VISIBLE
        } else {
            invoice_action_accept.visibility = View.VISIBLE
            invoice_action_return.visibility = View.GONE
            invoice_action_submit.visibility = View.GONE
        }

        if (uiState.invoice.comment.isNullOrEmpty()) {
            invoice_comment.visibility = View.GONE
            invoice_label_comment.visibility = View.GONE
        } else {
            invoice_comment.visibility = View.VISIBLE
            invoice_comment.text = uiState.invoice.comment
            invoice_label_comment.visibility = View.VISIBLE
        }

        searchView.visibility = if (uiState.searchViewOpen) View.VISIBLE else View.GONE
        searchView.setQuery(uiState.searchRequest, false)
        if (uiState.setFocusToSearchView) {
            searchView.isIconified = false
            searchView.requestFocusFromTouch()
        }

        if (searchView.isVisible && uiState.searchRequest.isNotBlank())
            itemsAdapter.filter(uiState.searchRequest)
        else
            itemsAdapter.filter(null)
    }

    override fun onRenderUiEffect(uiEffect: InvoiceDetailUiEffect) {
        when (uiEffect) {
            is InvoiceDetailUiEffect.ProductClick -> {
                sessionManager.setProduct(uiEffect.product)
                reloadDataOnResume = true
                findNavController().navigate(R.id.action_destination_invoiceDetail_to_productDetailFragment)
            }
            is InvoiceDetailUiEffect.Error -> {
                Timber.e("Network error: ${uiEffect.gatewayError?.code} - ${uiEffect.gatewayError?.message}")
                uiEffect.gatewayError?.exception?.also { Timber.e(Log.getStackTraceString(it)) }
                Toast.makeText(context, getErrorMessageForUi(resources, uiEffect.gatewayError), Toast.LENGTH_LONG)
                    .show()
            }
            is InvoiceDetailUiEffect.SuccessMessage ->
                Toast.makeText(context, R.string.success_exclamation, Toast.LENGTH_LONG).show()
            is InvoiceDetailUiEffect.NotAcceptedYetMessage ->
                Toast.makeText(context, R.string.invoice_accept_before_editing, Toast.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(FRAGMENT_STATE_RELOAD_DATA_ON_RESUME, reloadDataOnResume)
    }

    override fun onStop() {
        super.onStop()

        disposables.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear the reference to the adapter to prevent FastAdapter leaking this layout
        products_recyclerView.adapter = null
    }
}