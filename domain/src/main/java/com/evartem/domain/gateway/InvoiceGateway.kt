package com.evartem.domain.gateway

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Result
import io.reactivex.Observable

/**
 * The interface that use cases utilize to interact with data sources.
 *
 */
interface InvoiceGateway {

    /**
     * Get the invoices that the [user] is allowed to process or has already accepted for processing.
     *
     * @property user the user that is currently logged in
     * @property refresh force updating the data from the remote data source. False - return locally cached data.
     * @return the list of invoices as [InvoiceGatewayResult.Invoices] (with an optional error info inside
     * if refresh=true and request to the server failed) or [InvoiceGatewayResult.Error] if there's no locally cached
     * invoices and request to the server failed, or if failed to process the server's response.
     */
    fun getInvoicesForUser(user: User, refresh: Boolean = false): Observable<InvoiceGatewayResult>

    /**
     * Look for the specified invoice ([invoiceId]) in the local data source.
     *
     * @return [InvoiceGatewayResult.Invoice] if the request succeeded or [InvoiceGatewayResult.Error] otherwise
     */
    fun getInvoice(invoiceId: String): Observable<InvoiceGatewayResult>

    /**
     * Look for the specified product ([invoiceId].[productId]) in the local data source.
     *
     * @return [InvoiceGatewayResult.Product] if the request succeeded or [InvoiceGatewayResult.Error] otherwise
     */
    fun getProduct(invoiceId: String, productId: Int): Observable<InvoiceGatewayResult>

    /**
     * Add or update the specified result ([invoiceId].[productId].[result]) to/in the local data source.
     *
     * @return [InvoiceGatewayResult.ResultOperationSucceeded] if the request succeeded or
     * [InvoiceGatewayResult.Error] otherwise
     */
    fun insertOrUpdateResult(invoiceId: String, productId: Int, result: Result): Observable<InvoiceGatewayResult>

    /**
     * Delete the specified result ([invoiceId].[productId].[resultId]) in the local data source.
     *
     * @return [InvoiceGatewayResult.ResultOperationSucceeded] if the request succeeded or
     * [InvoiceGatewayResult.Error] otherwise
     */
    fun deleteResult(invoiceId: String, productId: Int, resultId: Int): Observable<InvoiceGatewayResult>

    /**
     * Ask the server if the [user] is allowed to process the invoice ([invoiceId]).
     *
     * @return [InvoiceGatewayResult.AcceptConfirmed] if the request succeeded or [InvoiceGatewayResult.Error] otherwise
     */
    fun requestInvoiceForProcessing(user: User, invoiceId: String): Observable<InvoiceGatewayResult>

    /**
     * Return the invoice back to the server so someone else could request it for processing.
     *
     * @return [InvoiceGatewayResult.ReturnConfirmed] if the request succeeded or [InvoiceGatewayResult.Error] otherwise
     */
    fun requestInvoiceReturn(user: User, invoiceId: String): Observable<InvoiceGatewayResult>

    /**
     * Submit completely or partially processed invoice to the server.
     *
     * @return [InvoiceGatewayResult.SubmitSucceeded] if the server received the result successfully or
     * [InvoiceGatewayResult.Error] otherwise
     */
    fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Observable<InvoiceGatewayResult>
}