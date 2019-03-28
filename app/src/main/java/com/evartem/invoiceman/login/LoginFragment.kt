package com.evartem.invoiceman.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.evartem.invoiceman.R
import com.evartem.invoiceman.base.AppBarFragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : AppBarFragment() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_invoices)
        }
    }

    override fun onConfigureBottomAppBar(bottomAppBar: BottomAppBar, fab: FloatingActionButton) {
        fab.hide()
        bottomAppBar.visibility = View.GONE
    }
}
