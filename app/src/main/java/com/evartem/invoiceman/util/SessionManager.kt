package com.evartem.invoiceman.util

import com.evartem.domain.entity.auth.User

/**
 * Stores information about the current session.
 */
// TODO: Save and restore data from/to sharedPrefs
class SessionManager {
    var currentUser: User = User()
    var currentInvoiceId: String = ""
    var currentProductId: Int = 0
}