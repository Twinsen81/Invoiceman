package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.repository.mapper.InvoiceMapperToRepoResult
import io.reactivex.Observable

class InvoiceRepository(
    private val localDataSource: InvoiceLocalDataSource,
    private val remoteDataSource: InvoiceService,
    private val mapperToRepoResult: InvoiceMapperToRepoResult
) {

    fun insertInvoices(invoices: List<InvoiceLocalModel>) {
    }

    fun insertInvoice(invoice: InvoiceLocalModel) {
    }

    fun insertResult(result: ResultLocalModel) {
    }

    fun getInvoicesForUser(userId: String, refresh: Boolean = false): Observable<InvoiceRepositoryResult> {
        val localResult = localDataSource.getInvoices()
            .filter { it.isNotEmpty() && !refresh }
            .map { invoiceList -> mapperToRepoResult.localToResult(invoiceList) }
            .toObservable()

        val remoteResult = remoteDataSource.getInvoicesForUser(userId)
            .map { invoiceList -> mapperToRepoResult.remoteToResult(invoiceList) }
            .doOnSuccess {
                if (it is InvoiceRepositoryResult.InvoicesRequestResult)
                    localDataSource.insertOrUpdateInvoice(
                        it.invoices.filter { invoice ->
                            invoice.processedByUser != userId // Do not overwrite invoices processed by this user
                        })
            }
            .toObservable()

        return Observable.concat(localResult, remoteResult)
            .firstElement()
            .toObservable()
    }
}