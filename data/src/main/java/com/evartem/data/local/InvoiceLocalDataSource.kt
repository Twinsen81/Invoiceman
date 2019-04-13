package com.evartem.data.local

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.where

/**
 * The implementation of a local data source for the repository pattern. Stores invoices locally on the device
 * as Realm DB objects.
 */
class InvoiceLocalDataSource {

    val isEmpty
        get() = Realm.getDefaultInstance().isEmpty

    /**
     * Get the invoice by its ID.
     */
    fun getInvoice(invoiceId: String): Single<InvoiceLocalModel> {
        var invoice = InvoiceLocalModel()
        Realm.getDefaultInstance().use { realm ->
            // A detached from Realm copy of the invoice that won't be updated by Realm anymore
            invoice = realm.copyFromRealm(
                realm.where<InvoiceLocalModel>().equalTo("id", invoiceId).findFirst()!!
            )
        }
        // Realm does not restore order of items in a list -> sort manually to restore the ascending order
        invoice.sortProducts()
        return Single.just(invoice)
    }

    /**
     * Mark the invoice as being processed by the user.
     * If the [userId] is empty, the invoice is marked as not being processed.
     */
    fun assignInvoiceToUser(userId: String, invoiceId: String) {
        Realm.getDefaultInstance().use { realm ->
            // A detached from Realm copy of the invoice that won't be updated by Realm anymore
            val invoice = realm.where<InvoiceLocalModel>().equalTo("id", invoiceId).findFirst()
            if (invoice != null)
                realm.executeTransaction {
                    invoice.processedByUser = userId
                }
        }
    }

    /**
     * Get all invoices in the DB.
     */
    fun getInvoices(): Single<List<InvoiceLocalModel>> {
        var invoices: List<InvoiceLocalModel> = listOf()
        Realm.getDefaultInstance().use { realm ->
            // A detached from Realm copy of invoices that won't be updated by Realm anymore
            invoices = realm.copyFromRealm(realm.where<InvoiceLocalModel>().findAll())
        }
        // Realm does not restore order of items in a list -> sort manually to restore the ascending order
        invoices.forEach { it.sortProducts() }
        return Single.just(invoices)
    }

    /**
     * Delete all invoices from the DB, then insert the provided ones.
     */
    fun deleteAllAndInsert(invoices: List<InvoiceLocalModel>) =
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.deleteAll()
                it.insert(invoices)
            }
        }

    /**
     * Insert or update (if already exists) one invoice.
     */
    fun insertOrUpdateInvoice(invoice: InvoiceLocalModel) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.insertOrUpdate(invoice)
            }
        }
    }

    /**
     * Insert or update (if already exist) all invoices in the DB.
     */
    fun insertOrUpdateInvoices(invoices: List<InvoiceLocalModel>) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.insertOrUpdate(invoices)
            }
        }
    }

    /**
     * Insert or update (if already exists) one result for the given product.
     */
    fun insertOrUpdateResult(invoiceId: String, productId: Int, result: ResultLocalModel) {
        doForProduct(invoiceId, productId) {
            it.insertOrUpdateResult(result)
        }
    }

    /**
     * Delete one result for the given product.
     */
    fun deleteResult(invoiceId: String, productId: Int, result: ResultLocalModel) {
        doForProduct(invoiceId, productId) {
            it.deleteResult(result)
        }
    }

    /**
     * Delete all results for the given product.
     */
    fun deleteAllResults(invoiceId: String, productId: Int) {
        doForProduct(invoiceId, productId) {
            it.deleteAllResults()
        }
    }

    /**
     * Finds the given product in the DB and performs the given action on it within a Realm transaction (write access).
     *
     * @param invoiceId the invoice to find the product in
     * @param productId the product to perform the action on
     * @param block the action to perform on the product
     */
    private fun doForProduct(invoiceId: String, productId: Int, block: (ProductLocalModel) -> Unit) {
        Realm.getDefaultInstance().use { realm ->
            val invoice = realm.where<InvoiceLocalModel>().equalTo("id", invoiceId).findFirst()
            val product =
                invoice?.products?.findLast { product -> product.id == productId }
                    ?: throw IllegalArgumentException("Unable to find the product with id=$productId for invoice id=$invoiceId")

            realm.executeTransaction {
                block(product)
            }
        }
    }
}