package com.evartem.domain.gateway

import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import io.reactivex.Observable

/**
 * The interface that use cases utilize to interact with data sources.
 *
 */
interface InvoiceGateway {

    /**
     * Returns invoices that the [user] is allowed to process or has already accepted for processing.
     * @property user the user that is currently logged in
     * @property refresh force updating the data from the remote data source. False - return locally cached data.
     *
     * @return the list of invoices as [InvoiceGatewayResult.Invoices]
     */
    fun getInvoicesForUser(user: User, refresh: Boolean = false): Observable<InvoiceGatewayResult>

    /**
     * Looks for the specified invoice ID in the local data source. Returns the corresponding invoice if found.
     */
    fun getInvoice(invoiceId: String): Observable<InvoiceGatewayResult>

    /**
     * @return [InvoiceGatewayResult.Invoices] or [InvoiceGatewayResult.Error]
     * Asks the server if the [user] is allowed to process the invoice ([invoiceId]).
     */
    fun requestInvoiceForProcessing(user: User, invoiceId: String): Observable<InvoiceGatewayResult>

    /**
     * Returns the invoice back to the server so someone else could request it for processing.
     */
    fun requestInvoiceReturn(user: User, invoiceId: String): Observable<InvoiceGatewayResult>

    /**
     * Submit completely or partially processed invoice to the server.
     */
    fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Observable<InvoiceGatewayResult>
}