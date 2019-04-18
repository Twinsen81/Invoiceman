package com.evartem.invoiceman.product.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.evartem.domain.entity.doc.Product
import com.evartem.domain.entity.doc.Result
import com.evartem.domain.entity.doc.ResultStatus
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.evartem.invoiceman.product.mvi.ProductDetailEvent
import com.evartem.invoiceman.product.mvi.ProductDetailUiEffect
import com.evartem.invoiceman.product.mvi.ProductDetailUiState
import com.evartem.invoiceman.product.mvi.ProductDetailViewModel
import com.evartem.invoiceman.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.jakewharton.rxbinding3.view.clicks
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.utils.DiffCallback
import com.mikepenz.fastadapter.commons.utils.FastAdapterDiffUtil
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.itemanimators.ScaleUpAnimator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.item_result.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ProductDetailFragment : MviFragment<ProductDetailUiState, ProductDetailUiEffect, ProductDetailEvent>() {

    companion object {
        const val RESULT_ITEM_TYPE_BASIC = 1
        lateinit var processingStatusBackground: ProcessingStatusBackground
    }

    private val viewModel by viewModel<ProductDetailViewModel>()

    private lateinit var itemsAdapter: ItemAdapter<ResultItem>

    private var disposables: CompositeDisposable = CompositeDisposable()

    // A subject to diff and render the recycler view asynchronously
    private val resultsObservable: PublishSubject<ProductDetailUiState> = PublishSubject.create()

    private val resultOperationEvents: PublishSubject<ProductDetailEvent> = PublishSubject.create()

    private lateinit var statusDialog: StatusDialog

    // Temporary workaround (see below)
    private var scanResult: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusDialog = StatusDialog(context!!)

        processingStatusBackground = ProcessingStatusBackground(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_product_detail, container, false)

    // KTX synthetic will work only from here (not in onCreateView)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setupBadgeHints()

        configureBottomAppBar()
    }

    private fun setupRecyclerView() {

        itemsAdapter = ItemAdapter()

        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        results_recyclerView.layoutManager = linearLayoutManager
        results_recyclerView.adapter =
            FastAdapter.with<IItem<*, *>, IAdapter<out IItem<*, *>>>(listOf(itemsAdapter))

        results_recyclerView.itemAnimator = ScaleUpAnimator()

        itemsAdapter.fastAdapter.withEventHook(object : ClickEventHook<ResultItem?>() {
            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): MutableList<View> {
                if (viewHolder is ResultItem.ViewHolder) {
                    return mutableListOf(
                        viewHolder.itemView.result_action_delete,
                        viewHolder.itemView.result_action_edit
                    )
                }
                return mutableListOf()
            }

            override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<ResultItem?>, item: ResultItem?) {
                if (v.id == R.id.result_action_delete)
                    resultOperationEvents.onNext(ProductDetailEvent.DeleteResult(item!!.result.id))
                if (v.id == R.id.result_action_edit)
                // resultOperationEvents.onNext(ProductDetailEvent.EditResult(item!!.result.id))
                    Toast.makeText(context, "Under construction...", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupBadgeHints() {
        product_article_scan_required.setOnClickListener {
            showHintSnackbar(R.string.product_scan_article_hint)
        }
        product_has_serial.setOnClickListener {
            showHintSnackbar(R.string.product_has_serial_hint)
        }
        product_serial_scan_required.setOnClickListener {
            showHintSnackbar(R.string.product_scan_serial_hint)
        }
        product_serial_same.setOnClickListener {
            showHintSnackbar(R.string.product_same_serial_hint)
        }
        product_serial_pattern_used.setOnClickListener {
            showHintSnackbar(R.string.product_serial_pattern_hint)
        }
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

    private fun showHintSnackbar(@StringRes resId: Int) {
        Snackbar.make(view!!, resId, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.got_it) {}
            .show()
    }

    override fun getUiStateObservable(): Observable<ProductDetailUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<ProductDetailUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (ProductDetailEvent) -> Unit = viewModel::addEvent

    override fun onStart() {
        super.onStart()

        setupRecyclerViewAsyncRenderingWithDiff()

        processScanResult()
    }

    private fun setupRecyclerViewAsyncRenderingWithDiff() {
        resultsObservable
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
        addUiEvent(resultOperationEvents)

        addUiEvent(
            fab.clicks()
                .throttleFirst(1, TimeUnit.SECONDS)
                .map { ProductDetailEvent.FabClick })
    }

    override fun onRenderUiState(uiState: ProductDetailUiState) {

        // Render the recycler view asynchronously with diffing
        if (uiState.product.getResults().isNotEmpty())
            resultsObservable.onNext(uiState)
        else // Diffing crashes when updating from an non-empty list -> empty
            itemsAdapter.clear()

        product_article.text = uiState.product.article
        product_description.text = uiState.product.description

        product_qty_done.text = uiState.product.getResults().size.toString()
        product_qty.text = uiState.product.quantity.toString()

        if (uiState.product.isProcessingFinished)
            fab.hide()
        else
            fab.show()

        product_info_panel.setBackgroundColor(
            when {
                uiState.product.isProcessingFinishedSuccessfully ->
                    processingStatusBackground.finishedWithoutErrors
                uiState.product.isProcessingFinishedWithErrors ->
                    processingStatusBackground.finishedWithErrors
                else -> processingStatusBackground.notEvenStarted
            }
        )

        product_article_scan_required.visibility =
            if (uiState.product.articleScanRequired) View.VISIBLE else View.GONE
        product_has_serial.visibility =
            if (uiState.product.hasSerialNumber) View.VISIBLE else View.GONE
        product_serial_scan_required.visibility =
            if (uiState.product.serialNumberScanRequired) View.VISIBLE else View.GONE
        product_serial_same.visibility =
            if (uiState.product.equalSerialNumbersAreOk) View.VISIBLE else View.GONE
        product_serial_pattern_used.visibility =
            if (uiState.product.serialNumberPattern != null) View.VISIBLE else View.GONE
    }

    override fun onRenderUiEffect(uiEffect: ProductDetailUiEffect) {
        when (uiEffect) {
            is ProductDetailUiEffect.StartScan -> {
                // getNextSimulatedResult(uiEffect.product)
                startBarcodeScanner()
            }

            is ProductDetailUiEffect.AddingResultFailed ->
                Toast.makeText(
                    context,
                    if (uiEffect.reason.isBlank()) R.string.result_add_failed.toString(resources) else uiEffect.reason,
                    Toast.LENGTH_LONG
                ).show()

            is ProductDetailUiEffect.Error -> {
                Timber.e("Network error: ${uiEffect.gatewayError?.code} - ${uiEffect.gatewayError?.message}")
                uiEffect.gatewayError?.exception?.also { Timber.e(Log.getStackTraceString(it)) }
                Toast.makeText(context, getErrorMessageForUi(resources, uiEffect.gatewayError), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    // TODO: DOES NOT WORK WELL! Replace this with a camera view inside your layout!
    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setPrompt(R.string.scan_prompt.toString(resources))
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            setScanResult(result.contents)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun setScanResult(barcode: String?) {
        scanResult = if (barcode.isNullOrBlank())
            Result(ResultStatus.FAILED, "0", R.string.result_add_failed.toString(resources), 0)
        else
            Result(ResultStatus.COMPLETED, barcode, null, 0)
    }

    /**
     * This is a temporary workaround until the scanning function is moved to a fragment.
     * onActivityResult is called BEFORE onStart -> the fragment won't be subscribed to the viewModel's
     * uiEffects observable when viewModel pushes a ui effect about the result of the scanning
     * operation. Thus, the ui effect is lost.
     */
    private fun processScanResult() {
        if (scanResult != null) {
            viewModel.addEvent(ProductDetailEvent.AddResult(scanResult!!))
            scanResult = null
        }
    }

    private fun simulateScanning(product: Product) {
        val serials = listOf("2384294238", "24323423423", "S2349-SFSDF-445", "GDFGDF-3534534", "S454444FF")
        val comments = listOf(
            "Broken packaging",
            "Missing manual",
            "No serial",
            "There's oil inside the box and it looks like the box was repackaged somewhere along the way"
        )
        val status = if (Random.nextBoolean()) ResultStatus.COMPLETED else ResultStatus.FAILED

        addNewResult(
            status, serials[Random.nextInt(serials.size)],
            if (status == ResultStatus.FAILED || Random.nextBoolean())
                comments[Random.nextInt(comments.size)]
            else null
        )
    }

    private fun addNewResult(status: ResultStatus, serial: String, comment: String? = null) {
        viewModel.addEvent(ProductDetailEvent.AddResult(Result(status, serial, comment, 0)))
    }

    override fun onStop() {
        super.onStop()

        disposables.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear the reference to the adapter to prevent FastAdapter leaking this layout
        results_recyclerView.adapter = null
    }
}