package com.evartem.invoiceman.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.evartem.domain.entity.auth.Group
import com.evartem.domain.entity.auth.Permission
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.auth.UserStatus
import com.evartem.invoiceman.R
import com.evartem.invoiceman.util.SessionManager
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private val sessionManager: SessionManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)



    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        buttonSignIn.setOnClickListener {
            buttonSignIn.isEnabled = false
            login()
            findNavController().navigate(R.id.action_invoices)
        }
    }

    // TODO: Implement authorization trough Auth0.com
    private fun login() {
        sessionManager.currentUser = User(
            if (email.text.isNotBlank()) email.text.toString() else "demo@invoiceman.com",
            "Demo Demov",
            "http://www.avatarsdb.com/avatars/einteins_tongue.jpg", "Warehouse worker",
            listOf(
                Group(
                    1,
                    "Simple worker",
                    listOf(
                        Permission.VIEW_INVOICE,
                        Permission.PROCESS_INVOICE,
                        Permission.ENTER_SERIAL_MANUALLY,
                        Permission.EDIT_RESULT
                    )
                )
            ),
            UserStatus.READY
        )
    }
}
