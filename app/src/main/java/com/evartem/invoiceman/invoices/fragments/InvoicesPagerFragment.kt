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
import com.evartem.invoiceman.navigation.BottomNavigationDrawerFragment
import com.evartem.invoiceman.util.getErrorMessageForUi
import com.jakewharton.rxbinding3.appcompat.itemClicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices_pager.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class InvoicesPagerFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    private val viewModel by viewModel<InvoicesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_invoices_pager, container, false)

    // KTX synthetic will work only from here (not in onCreateView)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabs()

        configureBottomAppBar()
    }

    private fun setupTabs() {
        val pages = listOf(
            resources.getString(R.string.invoices_tab_available) to createInvoicesAvailableFragment(),
            resources.getString(R.string.invoices_tab_in_progress) to createInvoicesInProgressFragment()
        )

        invoicesViewPager.adapter = InvoicesPagerAdapter(childFragmentManager, pages)
        invoicesTabs.setupWithViewPager(invoicesViewPager)
    }

    private fun createInvoicesAvailableFragment(): InvoicesFragment {
        val fragment = InvoicesFragment()
        fragment.arguments = InvoicesFragmentOptions().toBundle()
        return fragment
    }

    private fun createInvoicesInProgressFragment(): InvoicesFragment {
        val fragment = InvoicesFragment()
        fragment.arguments = InvoicesFragmentOptions(
            navigateToDetailOnClick = false,
            filterTypeInProgress = true,
            reloadDataOnResume = false
        ).toBundle()
        return fragment
    }

    private fun configureBottomAppBar() {
        bottomAppBar.navigationIcon = getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        bottomAppBar.replaceMenu(R.menu.invoices)
        fab.hide()

        bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
        }
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

    override fun onRenderUiState(uiState: InvoicesUiState) {

        val allowSortAndSearch = uiState.invoices.isNotEmpty() && !uiState.isRefreshing
        bottomAppBar.menu.findItem(R.id.invoices_sort).isEnabled = allowSortAndSearch
        bottomAppBar.menu.findItem(R.id.invoices_search).isEnabled = allowSortAndSearch
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

    // Just let the child fragments handle the Back event
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
