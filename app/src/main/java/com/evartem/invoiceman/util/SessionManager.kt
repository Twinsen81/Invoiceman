package com.evartem.invoiceman.util

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Product

/**
 * Stores information about the current session.
 */
// TODO: Save and restore data from/to sharedPrefs
class SessionManager {
    var currentUser: User = User()
    private var currentInvoice: Invoice? = null
    private var currentProduct: Product? = null

    fun setInvoice(invoice: Invoice)  {
        currentInvoice = invoice.copy()
    }

    fun getInvoice(): Invoice = currentInvoice!!
    fun getInvoiceId(): String = currentInvoice!!.id

    fun setProduct(product: Product)  {
        currentProduct = product.copy()
    }

    fun getProduct(): Product = currentProduct!!
    fun getProductId(): Int = currentProduct!!.id
}