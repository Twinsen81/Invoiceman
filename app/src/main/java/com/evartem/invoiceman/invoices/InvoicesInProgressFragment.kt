package com.evartem.invoiceman.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.evartem.invoiceman.R
import com.evartem.invoiceman.main.BaseFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_invoices_inprogress.*

class InvoicesInProgressFragment: BaseFragment() {

    private lateinit var viewModel: InvoicesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_invoices_inprogress, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(InvoicesViewModel::class.java)

        invoices_in_progress_gradientChart.chartValues = randomGradientChart()
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) = Unit

    override fun onDestroy() {
        super.onDestroy()
    }
}