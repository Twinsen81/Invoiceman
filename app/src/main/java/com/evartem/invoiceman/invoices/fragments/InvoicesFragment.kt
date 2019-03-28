package com.evartem.invoiceman.invoices.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.GatewayErrorCode
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.MviFragment
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesEvent
import com.evartem.invoiceman.invoices.mvi.InvoicesUiEffect
import com.evartem.invoiceman.invoices.mvi.InvoicesUiState
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        subscribeToViewModel()
    }

    private fun setupTabs() {
        val pages = listOf(
            resources.getString(R.string.invoices_tab_available) to InvoicesAvailableFragment(),
            resources.getString(R.string.invoices_tab_in_progress) to InvoicesInProgressFragment()
        )

        invoicesViewPager.adapter = InvoicesPagerAdapter(childFragmentManager, pages)
        invoicesTabs.setupWithViewPager(invoicesViewPager)
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        bottomAppBar.navigationIcon = getDrawable(context!!, R.drawable.ic_menu)
        bottomAppBar.visibility = View.VISIBLE
        fab.hide()
    }

    override fun onRenderUiEffect(uiEffect: InvoicesUiEffect) =
        when (uiEffect) {
            is InvoicesUiEffect.RemoteDatasourceError -> {
                Timber.e("Network error: ${uiEffect.gatewayError.code} - ${uiEffect.gatewayError.message}")
                uiEffect.gatewayError.exception?.also { Timber.e(Log.getStackTraceString(it)) }
                Toast.makeText(context, getNetworkErrorMessageForUi(uiEffect.gatewayError), Toast.LENGTH_LONG).show()
            }
            is InvoicesUiEffect.NoNewData ->
                Toast.makeText(context, R.string.invoices_no_new_received, Toast.LENGTH_LONG).show()
        }

    override fun getUiStateObservable(): Observable<InvoicesUiState>? = null

    override fun getUiEffectObservable(): Observable<InvoicesUiEffect>? = viewModel.uiEffects

    override fun getUiEventsConsumer(): (InvoicesEvent) -> Unit = viewModel::addEvent

    private fun getNetworkErrorMessageForUi(gatewayError: GatewayError): String =
        when (gatewayError.code) {
            GatewayErrorCode.INCONSISTENT_DATA -> R.string.network_error_inconsistent_data.resToString()
            GatewayErrorCode.NO_PERMISSIONS -> R.string.network_error_no_permissions.resToString()
            GatewayErrorCode.NOT_FOUND -> R.string.network_error_not_found.resToString()
            GatewayErrorCode.ALREADY_TAKEN_BY_OTHER -> R.string.network_error_taken.resToString()
            GatewayErrorCode.INTERNAL_SERVER_ERROR -> R.string.network_error_server.resToString()
            GatewayErrorCode.GENERAL_NETWORK_ERROR -> R.string.network_error_server_not_available.resToString()
            GatewayErrorCode.WRONG_SERVER_RESPONSE -> R.string.network_error_server_wrong_response.resToString()
            else -> R.string.network_error_general.resToString().format(gatewayError.code.value)
        }

    private fun Int.resToString() = resources.getString(this)
}
