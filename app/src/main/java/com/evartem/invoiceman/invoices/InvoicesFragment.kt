package com.evartem.invoiceman.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.ViewModelProviders
import com.evartem.invoiceman.R
import com.evartem.invoiceman.main.BaseFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_invoices.*

class InvoicesFragment : BaseFragment() {

    private lateinit var viewModel: InvoicesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invoices, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(InvoicesViewModel::class.java)

        setupTabs()
    }

    private fun setupTabs() {
        invoicesViewPager.adapter = InvoicesPagerAdapter(fragmentManager!!, context!!)
        invoicesTabs.setupWithViewPager(invoicesViewPager)
        //invoicesTabs.setTabTextColors(Color.WHITE, Color.GREEN)

    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        bottomAppBar.navigationIcon = getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        fab.visibility = View.GONE

    }
}
