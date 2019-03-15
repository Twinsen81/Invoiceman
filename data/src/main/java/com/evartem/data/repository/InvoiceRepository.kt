package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.repository.mapper.InvoiceMapperToRepoResult
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

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

        var refreshFromServer = refresh
        if (localDataSource.isEmpty) refreshFromServer = true

        val localResult = localDataSource.getInvoices()
            .map { invoiceList -> mapperToRepoResult.localToResult(invoiceList) }
            .toObservable()

        val remoteResult =
            if (refreshFromServer)
                remoteDataSource.getInvoicesForUser(userId)
                    .map { invoiceList -> mapperToRepoResult.remoteToResult(invoiceList) }
                    .toObservable()
            else
                Observable.just(mapperToRepoResult.emptyResult)

        return Observable.zip(localResult, remoteResult,
            BiFunction { local: InvoiceRepositoryResult, remote: InvoiceRepositoryResult ->
                combineLocalAndRemoteResults(local, remote, userId)
            })
    }

    /**
     * Combines invoices from two data sources: local cached list and a response from the server.
     * Invoices from the server overwrite locally stored invoices unless those are being processed
     * by the user.
     *
     * The response code is always taken from the server.
     *
     * @param local invoices (wrapped in InvoiceRepositoryResult) stored locally on the device
     * @param remote invoices (wrapped in InvoiceRepositoryResult) received from the server
     * @param userId the current user's ID
     */
    private fun combineLocalAndRemoteResults(
        local: InvoiceRepositoryResult, remote: InvoiceRepositoryResult, userId: String
    ): InvoiceRepositoryResult {

        val localInvoices = (local as InvoiceRepositoryResult.InvoicesRequestResult).invoices
        val remoteInvoices = (remote as InvoiceRepositoryResult.InvoicesRequestResult).invoices
        val unitedInvoices: MutableList<InvoiceLocalModel> = mutableListOf()

        // Keep local invoices that are:
        localInvoices.filter { localInvoice ->
            localInvoice.processedByUser == userId || // being processed by this user
                    (localInvoice.processedByUser != userId && remoteInvoices // not being processed but are available in the remote list
                        .any { remoteInvoice -> localInvoice.id == remoteInvoice.id })
        }.toCollection(unitedInvoices)

        // Add remote invoices that are not yet stored locally
        remoteInvoices.filter { remoteInvoice ->
            localInvoices.none { localInvoice -> localInvoice.id == remoteInvoice.id }
        }

        return InvoiceRepositoryResult.InvoicesRequestResult(unitedInvoices, remote.response, remote.networkError)
    }
}
