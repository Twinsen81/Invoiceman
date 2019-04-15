package com.evartem.invoiceman.product.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.evartem.invoiceman.product.mvi.ProductDetailEvent
import com.evartem.invoiceman.product.mvi.ProductDetailUiEffect
import com.evartem.invoiceman.product.mvi.ProductDetailUiState
import com.evartem.invoiceman.product.mvi.ProductDetailViewModel
import com.evartem.invoiceman.util.StatusDialog
import com.evartem.invoiceman.util.getErrorMessageForUi
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
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_detail.bottomAppBar
import kotlinx.android.synthetic.main.fragment_product_detail.fab
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProductDetailFragment : MviFragment<ProductDetailUiState, ProductDetailUiEffect, ProductDetailEvent>() {

    companion object {
        const val RESULT_ITEM_TYPE_BASIC = 1
    }

    private val viewModel by viewModel<ProductDetailViewModel>()

    private lateinit var itemsAdapter: ItemAdapter<ResultItem>

    private var disposables: CompositeDisposable = CompositeDisposable()
    private val uiStates: PublishSubject<ProductDetailUiState> = PublishSubject.create()

    private lateinit var statusDialog: StatusDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_product_detail, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupRecyclerViewAsyncRenderingWithDiff()

        configureBottomAppBar()

        statusDialog = StatusDialog(context!!)
    }

    private fun configureBottomAppBar() {
        bottomAppBar.navigationIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        fab.show()

        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }

    private fun setupRecyclerView() {

        itemsAdapter = ItemAdapter()

        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        results_recyclerView.layoutManager = linearLayoutManager
        results_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter))

        results_recyclerView.itemAnimator = ScaleUpAnimator()
    }

    private fun setupRecyclerViewAsyncRenderingWithDiff() {
        uiStates
            .subscribeOn(Schedulers.io())
            .map { it.product.getResults() }
            .flatMap { listOfResults ->
                Observable.fromIterable(listOfResults)
                    .map { item -> ResultItem(item) }
                    .toList()
                    .toObservable()
            }
            .map {
                FastAdapterDiffUtil.calculateDiff(itemsAdapter, it, object : DiffCallback<ResultItem?> {
                    override fun areItemsTheSame(oldItem: ResultItem?, newItem: ResultItem?) =
                        newItem?.result?.id == oldItem?.result?.id

                    override fun getChangePayload(
                        oldItem: ResultItem?,
                        oldItemPosition: Int,
                        newItem: ResultItem?,
                        newItemPosition: Int
                    ): Any? = null

                    override fun areContentsTheSame(oldItem: ResultItem?, newItem: ResultItem?) =
                        oldItem?.result == newItem?.result
                })
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FastAdapterDiffUtil.set(itemsAdapter, it)
                results_recyclerView.layoutManager?.scrollToPosition(0)
            }.addTo(disposables)
    }

    override fun onSetupUiEvents() {
    }

    override fun onRenderUiState(uiState: ProductDetailUiState) {

        // Render the recycler view asynchronously with diffing
        if (uiState.product.getResults().isNotEmpty())
            uiStates.onNext(uiState)
        else // Diffing crashes when updating from an non-empty list -> empty
            itemsAdapter.clear()

        product_article.text = uiState.product.article
        product_description.text = uiState.product.description
        product_qty.text = uiState.product.quantity.toString()

        product_article_scan_required.visibility =
            if (uiState.product.articleScanRequired) View.VISIBLE else View.GONE
        product_has_serial.visibility =
            if (uiState.product.hasSerialNumber) View.VISIBLE else View.GONE
        product_serial_scan_required.visibility =
            if (uiState.product.serialNumberScanRequired) View.VISIBLE else View.GONE
        product_serial_same.visibility =
            if (uiState.product.equalSerialNumbersAreOk) View.VISIBLE else View.GONE
    }

    override fun onRenderUiEffect(uiEffect: ProductDetailUiEffect) {
        when (uiEffect) {
/*            is ProductDetailUiEffect.ProductClick -> {
                sessionManager.currentProductId = uiEffect.productId
                // findNavController().navigate(R.id.destination_invoiceDetail)
            }*/
            is ProductDetailUiEffect.Error -> {
                Timber.e("Network error: ${uiEffect.gatewayError?.code} - ${uiEffect.gatewayError?.message}")
                uiEffect.gatewayError?.exception?.also { Timber.e(Log.getStackTraceString(it)) }
                Toast.makeText(context, getErrorMessageForUi(resources, uiEffect.gatewayError), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun getUiStateObservable(): Observable<ProductDetailUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<ProductDetailUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (ProductDetailEvent) -> Unit = viewModel::addEvent

    override fun onDestroyView() {
        // Clear the reference to the adapter to prevent leaking this layout
        results_recyclerView.adapter = null

        disposables.clear()
        super.onDestroyView()
    }
}