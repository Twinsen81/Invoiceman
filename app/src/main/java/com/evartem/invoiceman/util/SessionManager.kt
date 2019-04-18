package com.evartem.invoiceman.util

import android.content.SharedPreferences
import androidx.core.content.edit
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Product

/**
 * Stores information about the current session.
 */
class SessionManager(private val sharedPrefs: SharedPreferences) {
    var currentUser: User = User()
    private var currentInvoiceId: String = ""
    private var currentProductId: Int = 0

    init {
        currentInvoiceId = sharedPrefs.getString("currentInvoiceId", "")!!
        currentProductId = sharedPrefs.getInt("currentProductId", 0)
    }

    fun setInvoice(invoice: Invoice) {
        currentInvoiceId = invoice.id
        sharedPrefs.edit { putString("currentInvoiceId", currentInvoiceId) }
    }

    fun getInvoiceId(): String = currentInvoiceId

    fun setProduct(product: Product) {
        currentProductId = product.id
        sharedPrefs.edit { putInt("currentProductId", currentProductId) }
    }

    fun getProductId(): Int = currentProductId
}