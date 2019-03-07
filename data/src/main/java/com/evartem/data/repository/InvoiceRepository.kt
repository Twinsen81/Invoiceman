package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.remote.model.InvoiceRemoteModel
import io.reactivex.Observable

class InvoiceRepository(private val localDataSource: InvoiceLocalDataSource,
                        private val remoteDataSource: InvoiceService) {

    fun insertInvoices(invoices: List<InvoiceLocalModel>) {
    }

    fun insertInvoice(invoice: InvoiceLocalModel) {

    }

    fun insertResult(result: ResultLocalModel) {

    }

    fun getInvoicesForUser(userId: String): Observable<List<InvoiceRemoteModel>> {

    }
}