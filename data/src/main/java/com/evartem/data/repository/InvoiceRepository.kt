package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.repository.mapper.InvoiceMapperToResult
import io.reactivex.Observable
import io.reactivex.Single

class InvoiceRepository(private val localDataSource: InvoiceLocalDataSource,
                        private val remoteDataSource: InvoiceService,
                        private val mapperToResult: InvoiceMapperToResult
) {

    fun insertInvoices(invoices: List<InvoiceLocalModel>) {
    }

    fun insertInvoice(invoice: InvoiceLocalModel) {

    }

    fun insertResult(result: ResultLocalModel) {

    }

    fun getInvoicesForUser(userId: String, refresh: Boolean = false): Single<InvoiceRepositoryResult> {
        val localResult = localDataSource.getInvoices()
            .filter { invoiceList -> !invoiceList.isEmpty() }
            .map { invoiceList -> mapperToResult.localToResult(invoiceList) }

        val remote = remoteDataSource.getInvoicesForUser(userId)
            .map { mapper.toLocal(it) }
            .doOnEvent { t1, t2 ->  }

        return Observable.just(refresh)
            .doOnNext { if (it) eventLocalDataSource.deleteByType(type) }
            .flatMap {
                Observable.concat(local, remote)
                    .firstElement()
                    .toObservable()
            }
    }
}