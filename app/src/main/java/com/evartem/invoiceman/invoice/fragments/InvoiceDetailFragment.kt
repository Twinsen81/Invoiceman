package com.evartem.invoiceman.invoice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailEvent
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailUiEffect
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailUiState
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailViewModel
import com.evartem.invoiceman.util.SessionManager
import com.evartem.invoiceman.util.StatusDialog
import com.evartem.invoiceman.util.hideKeyboard
import com.evartem.invoiceman.util.itemClicks
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
import java.util.concurrent.TimeUnit

class InvoiceDetailFragment : MviFragment<InvoiceDetailUiState, InvoiceDetailUiEffect, InvoiceDetailEvent>() {

    companion object {
        const val PRODUCT_ITEM_TYPE_BASIC = 1
    }

    private val viewModel by viewModel<InvoiceDetailViewModel>()
    private val sessionManager: SessionManager by inject()

    private lateinit var itemsAdapter: ItemAdapter<ProductItem>

    private var disposables: CompositeDisposable = CompositeDisposable()
    private val uiStates: PublishSubject<InvoiceDetailUiState> = PublishSubject.create()

    private lateinit var statusDialog: StatusDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(com.evartem.invoiceman.R.layout.fragment_invoice_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupRecyclerViewAsyncRenderingWithDiff()

        statusDialog = StatusDialog(context!!)

        setupUiEvents()

        subscribeToViewModel()
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

    private fun setupRecyclerViewAsyncRenderingWithDiff() {
        uiStates
            .subscribeOn(Schedulers.io())
            .map { it.invoice.products }
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

    private fun setupUiEvents() {

        addUiEvent(itemsAdapter.fastAdapter.itemClicks()
            .map { ProductItem -> InvoiceDetailEvent.Click(ProductItem.product.id) })

        addUiEvent(
            searchView.findViewById<ImageView>(com.evartem.invoiceman.R.id.search_close_btn).clicks()
                .doOnNext { hideKeyboard(activity!!) }
                .map { InvoiceDetailEvent.Search(stopSearch = true) })

        addUiEvent(
            searchView.queryTextChangeEvents()
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { searchEvent -> searchEvent.isSubmitted && searchEvent.queryText.isNotBlank() }
                .map { searchEvent -> InvoiceDetailEvent.Search(searchEvent.queryText.toString()) })
    }

    override fun onBackPressed(): Boolean =
        when {
            searchView.isVisible -> {
                hideKeyboard(activity!!)
                viewModel.addEvent(InvoiceDetailEvent.Search(stopSearch = true))
                true
            }
            else -> false
        }

    override fun onRenderUiState(uiState: InvoiceDetailUiState) {

        // Render asynchronously with diffing
        uiStates.onNext(uiState)

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

    }

    override fun onRenderUiEffect(uiEffect: InvoiceDetailUiEffect) {
        if (uiEffect is InvoiceDetailUiEffect.ProductClick) {
            sessionManager.currentProductId = uiEffect.productId
            //findNavController().navigate(R.id.destination_invoiceDetail)
        }
    }

    override fun getUiStateObservable(): Observable<InvoiceDetailUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoiceDetailUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (InvoiceDetailEvent) -> Unit = viewModel::addEvent

    override fun onDestroyView() {
        // Clear the reference to the adapter to prevent leaking this layout
        products_recyclerView.adapter = null

        disposables.clear()
        super.onDestroyView()
    }

}