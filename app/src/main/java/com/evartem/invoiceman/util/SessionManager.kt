package com.evartem.invoiceman.util

import com.evartem.domain.entity.auth.User

// TODO: Save and restore current user from/to sharedPrefs
class SessionManager {
    var currentUser: User = User()
    var currentInvoiceId: String = ""
    var currentProductId: Int = 0
}