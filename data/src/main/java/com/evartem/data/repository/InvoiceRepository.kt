package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.repository.mapper.InvoiceMapperToRepoResult
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Result
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * The repository pattern implementation that caches data locally on the device.
 *
 * @property localDataSource the local data source implementation
 * @property remoteDataSource the remote data source implementation
 * @property mapperToRepoResult a DTO mapper form remote to local and vice versa
 */
class InvoiceRepository(
    private val localDataSource: InvoiceLocalDataSource,
    private val remoteDataSource: InvoiceService,
    private val mapperToRepoResult: InvoiceMapperToRepoResult
) {

    /**
     * Get the invoice ([invoiceId]) from the local data source.
     *
     * returns either [InvoiceRepositoryResult.Invoice] or [InvoiceRepositoryResult.Error] if the given
     * [invoiceId] isn't found.
     */
    fun getInvoice(invoiceId: String): Observable<InvoiceRepositoryResult> =
        try {
            localDataSource.getInvoice(invoiceId)
                .map { invoice -> InvoiceRepositoryResult.Invoice(invoice) as InvoiceRepositoryResult }
                .toObservable()
        } catch (exception: Throwable) {
            Observable.just(mapperToRepoResult.errorFromException(exception))
        }

    /**
     * Get the product ([invoiceId].[productId]) from the local data source.
     *
     * returns either [InvoiceRepositoryResult.Product] or [InvoiceRepositoryResult.Error] if the given
     * [invoiceId] or [productId] isn't found.
     */
    fun getProduct(invoiceId: String, productId: Int): Observable<InvoiceRepositoryResult> =
        try {
            localDataSource.getProduct(invoiceId, productId)
                .map { product -> InvoiceRepositoryResult.Product(product) as InvoiceRepositoryResult }
                .toObservable()
        } catch (exception: Throwable) {
            Observable.just(mapperToRepoResult.errorFromException(exception))
        }

    /**
     * Add or update the specified result ([invoiceId].[productId].[result]) to/in the local data source.
     *
     * returns either [InvoiceRepositoryResult.ResultOperationSucceeded] or [InvoiceRepositoryResult.Error]
     * if the given [invoiceId], [productId] or isn't found.
     */
    fun insertOrUpdateResult(
        invoiceId: String,
        productId: Int,
        result: ResultLocalModel
    ): Observable<InvoiceRepositoryResult> =
        try {
            localDataSource.insertOrUpdateResult(invoiceId, productId, result)
                .map { product -> InvoiceRepositoryResult.ResultOperationSucceeded(product) as InvoiceRepositoryResult }
                .toObservable()
        } catch (exception: Throwable) {
            Observable.just(mapperToRepoResult.errorFromException(exception))
        }

    /**
     * Delete the specified result ([invoiceId].[productId].[resultId]) from the local data source.
     *
     * returns either [InvoiceRepositoryResult.ResultOperationSucceeded] or [InvoiceRepositoryResult.Error]
     * if the given [invoiceId], [productId] or [resultId] isn't found.
     */
    fun deleteResult(invoiceId: String, productId: Int, resultId: Int): Observable<InvoiceRepositoryResult> =
        try {
            localDataSource.deleteResult(invoiceId, productId, resultId)
                .map { product -> InvoiceRepositoryResult.ResultOperationSucceeded(product) as InvoiceRepositoryResult }
                .toObservable()
        } catch (exception: Throwable) {
            Observable.just(mapperToRepoResult.errorFromException(exception))
        }

    /**
     * Get invoices that the user ([userId]) is allowed to process or has already accepted for processing.
     * the invoices are taken from the local data source unless [refresh] = true or the local data source is empty
     *
     * @return the list of invoices as [InvoiceRepositoryResult.Invoices] (with an optional error info inside
     * if [refresh]=true and request to the server failed) or [InvoiceRepositoryResult.Error] if there's no locally
     * cached invoices and request to the server failed, or if failed to process the server's response.
     */
    fun getInvoicesForUser(userId: String, refresh: Boolean = false): Observable<InvoiceRepositoryResult> {

        var refreshFromServer = refresh
        if (localDataSource.isEmpty) refreshFromServer = true

        val localResult = localDataSource.getInvoices()
            .map { invoiceList -> InvoiceRepositoryResult.Invoices(invoiceList) }
            .toObservable()

        val remoteResult =
            if (refreshFromServer) {
                try {
                    remoteDataSource.getInvoicesForUser(userId)
                        .map { invoiceList -> mapperToRepoResult.remoteToResult(invoiceList) }
                        .onErrorReturn { exception -> mapperToRepoResult.errorFromException(exception) }
                        .toObservable()
                } catch (exception: Throwable) {
                    // If exception is thrown while creating an Observable -> onErrorReturn isn't called
                    Observable.just(mapperToRepoResult.errorFromException(exception))
                }
            } else
                Observable.just(InvoiceRepositoryResult.Invoices(listOf()))

        // Unite th two data sources into one Observable
        return Observable.zip(localResult, remoteResult,
            BiFunction { local: InvoiceRepositoryResult, remote: InvoiceRepositoryResult ->
                joinLocalAndRemoteResults(local, remote, userId)
            })
            .doOnNext { result ->
                if (refreshFromServer)
                    localDataSource.deleteAllAndInsert((result as InvoiceRepositoryResult.Invoices).invoices)
            }
    }

    /**
     * Joins invoices from two data sources: local cached list and a response from the server.
     * Invoices from the server overwrite locally stored invoices unless those are being processed
     * by the user.
     *
     * The response code is always taken from the server.
     *
     * @param local invoices (wrapped in InvoiceRepositoryResult) stored locally on the device
     * @param remote invoices (wrapped in InvoiceRepositoryResult) received from the server
     * @param userId the current user's ID
     */
    private fun joinLocalAndRemoteResults(
        local: InvoiceRepositoryResult,
        remote: InvoiceRepositoryResult,
        userId: String
    ): InvoiceRepositoryResult {

        val localInvoices = (local as InvoiceRepositoryResult.Invoices).invoices
        val remoteInvoices =
            if (remote is InvoiceRepositoryResult.Invoices) remote.invoices else listOf()
        val unitedInvoices: MutableList<InvoiceLocalModel> = mutableListOf()

        // Keep local invoices that are:
        localInvoices.filter { localInvoice ->
            localInvoice.processedByUser == userId || // 1) being processed by this user
                    // 2) not being processed but are not present in the remote list (e.g. the server is down)
                    (localInvoice.processedByUser != userId && remoteInvoices
                        .none { remoteInvoice -> localInvoice.id == remoteInvoice.id })
        }.toCollection(unitedInvoices)

        // Add remote invoices that are not yet stored locally
        remoteInvoices.filter { remoteInvoice ->
            unitedInvoices.none { localInvoice -> localInvoice.id == remoteInvoice.id }
        }.toCollection(unitedInvoices)

        return InvoiceRepositoryResult.Invoices(
            unitedInvoices,
            if (remote is InvoiceRepositoryResult.Error) remote.gatewayError else null
        )
    }

    /**
     * Ask the server if the [user] is allowed to process the invoice ([invoiceId]).
     *
     * @return [InvoiceRepositoryResult.AcceptConfirmed] if the request succeeded or [InvoiceRepositoryResult.Error] otherwise
     */
    fun requestInvoiceForProcessing(user: User, invoiceId: String): Observable<InvoiceRepositoryResult> =
        try {
            remoteDataSource.requestInvoiceForProcessing(user.id, invoiceId)
                .map { response -> mapperToRepoResult.remoteAcceptToResult(response) }
                .onErrorReturn { exception -> mapperToRepoResult.errorFromException(exception) }
                .doOnEvent { result, _ -> assignInvoiceToUserInLocalDatasource(result, user.id, invoiceId) }
                .toObservable()
        } catch (exception: Throwable) {
            // If exception is thrown while creating an Observable -> onErrorReturn isn't called
            Observable.just(mapperToRepoResult.errorFromException(exception))
        }

    /**
     * Return the invoice back to the server so someone else could request it for processing.
     *
     * @return [InvoiceRepositoryResult.ReturnConfirmed] if the request succeeded or [InvoiceRepositoryResult.Error] otherwise
     */
    fun requestInvoiceReturn(user: User, invoiceId: String): Observable<InvoiceRepositoryResult> =
        try {
            remoteDataSource.requestInvoiceReturn(user.id, invoiceId)
                .map { response -> mapperToRepoResult.remoteReturnToResult(response) }
                .onErrorReturn { exception -> mapperToRepoResult.errorFromException(exception) }
                .doOnEvent { result, _ -> assignInvoiceToUserInLocalDatasource(result, "", invoiceId) }
                .toObservable()
        } catch (exception: Throwable) {
            // If exception is thrown while creating an Observable -> onErrorReturn isn't called
            Observable.just(mapperToRepoResult.errorFromException(exception))
        }

    /**
     * Perform the "request for processing"/"request return" actions in the local data source.
     */
    private fun assignInvoiceToUserInLocalDatasource(
        requestResult: InvoiceRepositoryResult,
        userId: String,
        invoiceId: String
    ) =
        when (requestResult) {
            is InvoiceRepositoryResult.AcceptConfirmed,
            is InvoiceRepositoryResult.ReturnConfirmed ->
                localDataSource.assignInvoiceToUser(userId, invoiceId)
            else -> Unit
        }
}
