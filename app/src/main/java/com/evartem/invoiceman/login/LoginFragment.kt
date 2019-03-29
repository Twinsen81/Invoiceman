package com.evartem.invoiceman.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.AppBarFragment
import com.evartem.invoiceman.util.SessionManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class LoginFragment : AppBarFragment() {

    private lateinit var viewModel: LoginViewModel
    private val sessionManager: SessionManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        btnLogin.setOnClickListener {
            sessionManager.currentUser = get() // injecting DEMO_USER
            findNavController().navigate(R.id.action_invoices)
        }
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        fab.hide()
        bottomAppBar.visibility = View.GONE
    }
}
