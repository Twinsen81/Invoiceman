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
    // private var currentInvoice: Invoice? = null
    private var currentInvoiceId: String = ""
    // private var currentProduct: Product? = null
    private var currentProductId: Int = 0

    init {
        currentInvoiceId = sharedPrefs.getString("currentInvoiceId", "")!!
        currentProductId = sharedPrefs.getInt("currentProductId", 0)
    }

    fun setInvoice(invoice: Invoice) {
        // currentInvoice = invoice.copy()
        currentInvoiceId = invoice.id
        sharedPrefs.edit { putString("currentInvoiceId", currentInvoiceId) }
    }

    // fun getInvoice(): Invoice = currentInvoice!!
    // fun getInvoiceId(): String = currentInvoice!!.id
    fun getInvoiceId(): String = currentInvoiceId

    fun setProduct(product: Product) {
//        currentProduct = product.copy()
        currentProductId = product.id
        sharedPrefs.edit { putInt("currentProductId", currentProductId) }
    }

    // fun getProduct(): Product = currentProduct!!
//    fun getProductId(): Int = currentProduct!!.id
    fun getProductId(): Int = currentProductId
}