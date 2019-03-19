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
import kotlinx.android.synthetic.main.fragment_invoices_available.*

class InvoicesAvailableFragment : BaseFragment() {

    private lateinit var viewModel: InvoicesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_invoices_available, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(InvoicesViewModel::class.java)

        invoices_available_gradientChart.chartValues = randomGradientChart()
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) = Unit

    private fun render(uiState: InvoicesUiState) {
        invoices_available_text.text = "INVOICES: ${uiState.invoices.size}"
        invoices_available_loading.visibility = if (uiState.loadingIndicator) View.VISIBLE else View.GONE
    }
}