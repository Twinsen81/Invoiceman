package com.evartem.data.local

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ResultLocalModel
import io.reactivex.Single
import io.realm.Realm

class InvoiceLocalDataSource {

    fun getInvoices(): Single<List<InvoiceLocalModel>> {
        var invoices: List<InvoiceLocalModel> = listOf()
        Realm.getDefaultInstance().use {realm ->
            invoices = realm.where(InvoiceLocalModel::class.java).findAll()
        }
        return Single.just(invoices)
    }

    fun insertInvoices(invoices: List<InvoiceLocalModel>) {
    }


    fun insertInvoice(invoice: InvoiceLocalModel) {

    }

    fun insertResult(result: ResultLocalModel) {

    }

    fun deleteResult(result: ResultLocalModel) {

    }

    fun deleteAllResults() {

    }
}