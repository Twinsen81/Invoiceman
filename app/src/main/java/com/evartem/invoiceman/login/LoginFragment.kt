package com.evartem.invoiceman.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.evartem.invoiceman.R
import com.evartem.invoiceman.util.SessionManager
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private val sessionManager: SessionManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        // TODO: Implement authorization trough Auth0.com
        buttonSignIn.setOnClickListener {
            buttonSignIn.isEnabled = false
            sessionManager.currentUser = get() // injecting DEMO_USER
            findNavController().navigate(R.id.action_invoices)
        }
    }
}
