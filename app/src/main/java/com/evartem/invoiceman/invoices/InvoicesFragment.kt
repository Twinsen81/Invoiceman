package com.evartem.invoiceman.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.ViewModelProviders
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.BaseFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_invoices.*
import timber.log.Timber

class InvoicesFragment : BaseFragment() {

    private lateinit var viewModel: InvoicesViewModel

    private var viewModelDisposables: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_invoices, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(InvoicesViewModel::class.java)
        subscribeToViewModel()

        setupTabs()
    }

    private fun setupTabs() {
        val pages = listOf(
            Pair(resources.getString(R.string.invoices_tab_available), InvoicesAvailableFragment()),
            Pair(resources.getString(R.string.invoices_tab_in_progress), InvoicesInProgressFragment())
        )

        invoicesViewPager.adapter = InvoicesPagerAdapter(childFragmentManager, pages)
        invoicesTabs.setupWithViewPager(invoicesViewPager)
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        bottomAppBar.navigationIcon = getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        fab.hide()
    }

    private fun renderEffect(uiEffect: InvoicesUiEffect) {
      if (uiEffect is InvoicesUiEffect.NetworkError)
          Toast.makeText(context, uiEffect.message, Toast.LENGTH_LONG).show()
    }

    private fun subscribeToViewModel() {
        viewModel.uiEffects
            .doOnNext { Timber.d("MVI-New effect: $it") }
            .subscribe(::renderEffect) { Timber.wtf("MVI-Critical app error while processing UI effect") }
            .addTo(viewModelDisposables)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelDisposables.clear()
    }

}
