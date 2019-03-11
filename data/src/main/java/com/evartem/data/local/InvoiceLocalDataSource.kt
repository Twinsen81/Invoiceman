package com.evartem.data.local

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import io.reactivex.Single
import io.realm.Realm
import io.realm.kotlin.where

class InvoiceLocalDataSource {

    /**
     * Returns invoices that were saved locally on the device
     */
    fun getInvoices(): Single<List<InvoiceLocalModel>> {
        var invoices: List<InvoiceLocalModel> = listOf()
        Realm.getDefaultInstance().use { realm ->
            // A detached from Realm copy of invoices that won't be updated by Realm anymore
            invoices = realm.copyFromRealm(realm.where<InvoiceLocalModel>().findAll())
        }
        invoices.forEach { it.sortProducts() } // Realm does not restore order of items in a list -> sort manually to restore the ascending order
        return Single.just(invoices)
    }

    fun insertOrUpdateInvoice(invoice: InvoiceLocalModel) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.insertOrUpdate(invoice)
            }
        }
    }

    fun insertOrUpdateInvoice(invoices: List<InvoiceLocalModel>) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.insertOrUpdate(invoices)
            }
        }
    }

    fun insertOrUpdateResult(invoiceId: String, productId: Int, result: ResultLocalModel) {
        doForProduct(invoiceId, productId) {
            it.insertOrUpdateResult(result)
        }
    }

    fun deleteResult(invoiceId: String, productId: Int, result: ResultLocalModel) {
        doForProduct(invoiceId, productId) {
            it.deleteResult(result)
        }
    }

    fun deleteAllResults(invoiceId: String, productId: Int) {
        doForProduct(invoiceId, productId) {
            it.deleteAllResults()
        }
    }

    private fun doForProduct(invoiceId: String, productId: Int, block: (ProductLocalModel) -> Unit) {
        Realm.getDefaultInstance().use { realm ->
            val invoice = realm.where<InvoiceLocalModel>().equalTo("id", invoiceId).findFirst()
            val product =
                invoice?.products?.findLast { product -> product.id == productId }
                    ?: throw IllegalArgumentException("Unable to find the product with id=$productId for invoice id=$invoiceId")

            block(product)
            realm.executeTransaction {
                it.insertOrUpdate(invoice)
            }
        }
    }
}