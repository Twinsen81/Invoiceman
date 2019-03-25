package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.ViewModelProviders
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.*
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoices.*

class InvoicesFragment : MviFragment<InvoicesUiState, InvoicesUiEffect, InvoicesEvent>() {

    private lateinit var viewModel: InvoicesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(InvoicesViewModel::class.java)
        return inflater.inflate(R.layout.fragment_invoices, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupTabs()
    }

    private fun setupTabs() {
        val pages = listOf(
            Pair(resources.getString(R.string.invoices_tab_available),
                InvoicesAvailableFragment()
            ),
            Pair(resources.getString(R.string.invoices_tab_in_progress),
                InvoicesInProgressFragment()
            )
        )

        invoicesViewPager.adapter =
            InvoicesPagerAdapter(childFragmentManager, pages)
        invoicesTabs.setupWithViewPager(invoicesViewPager)
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        bottomAppBar.navigationIcon = getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        fab.hide()
    }

    override fun onRenderUiEffect(uiEffect: InvoicesUiEffect) {
        if (uiEffect is InvoicesUiEffect.NetworkError)
            Toast.makeText(context, uiEffect.message, Toast.LENGTH_LONG).show()
    }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = null

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent
}
