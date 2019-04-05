package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.mvi.*
import com.evartem.invoiceman.util.getErrorMessageForUi
import com.jakewharton.rxbinding3.appcompat.itemClicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class InvoicesFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    private val viewModel by viewModel<InvoicesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_invoices, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupTabs()

        configureBottomAppBar()
    }

    private fun setupTabs() {
        val pages = listOf(
            resources.getString(R.string.invoices_tab_available) to InvoicesAvailableFragment(),
            resources.getString(R.string.invoices_tab_in_progress) to InvoicesInProgressFragment()
        )

        invoicesViewPager.adapter = InvoicesPagerAdapter(childFragmentManager, pages)
        invoicesTabs.setupWithViewPager(invoicesViewPager)
    }

    override fun onSetupUiEvents() {
        addUiEvent(
            bottomAppBar.itemClicks()
                .map { item ->
                    when (item.itemId) {
                        R.id.invoices_search -> InvoicesEvent.Search(startSearch = true)
                        R.id.invoices_sort_by_number -> InvoicesEvent.Sort(SortInvoicesBy.NUMBER)
                        R.id.invoices_sort_by_date -> InvoicesEvent.Sort(SortInvoicesBy.DATE)
                        R.id.invoices_sort_by_seller -> InvoicesEvent.Sort(SortInvoicesBy.SELLER)
                        else -> InvoicesEvent.Empty
                    }
                }.filter { it !is InvoicesEvent.Empty })
    }

    override fun onRenderUiEffect(uiEffect: InvoicesUiEffect) =

        when (uiEffect) {
            is InvoicesUiEffect.Error -> {
                Timber.e("Network error: ${uiEffect.gatewayError?.code} - ${uiEffect.gatewayError?.message}")
                uiEffect.gatewayError?.exception?.also { Timber.e(Log.getStackTraceString(it)) }
                Toast.makeText(context, getErrorMessageForUi(resources, uiEffect.gatewayError), Toast.LENGTH_LONG)
                    .show()
            }
            is InvoicesUiEffect.NoNewData ->
                Toast.makeText(context, R.string.invoices_no_new_received, Toast.LENGTH_LONG).show()
            is InvoicesUiEffect.InvoiceClick -> Unit
        }

    private fun configureBottomAppBar() {
        bottomAppBar.navigationIcon = getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.replaceMenu(R.menu.invoices)
        fab.hide()
    }

    override fun onRenderUiState(uiState: InvoicesUiState) {

        val allowSortAndSearch = uiState.invoices.isNotEmpty() && !uiState.isRefreshing
        bottomAppBar.menu.findItem(R.id.invoices_sort).isEnabled = allowSortAndSearch
        bottomAppBar.menu.findItem(R.id.invoices_search).isEnabled = allowSortAndSearch
    }

    override fun onBackPressed(): Boolean {

        var isProcessed = false

        childFragmentManager.fragments.forEach { fragment ->
            if (fragment is MviFragment<*, *, *> && fragment.onBackPressed()) {
                isProcessed = true
                return@forEach
            }
        }

        return isProcessed
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = viewModel.uiState

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent
}
