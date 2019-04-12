package com.evartem.data.remote.api

import com.evartem.data.remote.model.InvoiceRemoteModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

/**
 * A Retrofit interface for communication with the backend.
 *
 */
interface InvoiceService {

    /**
     * Get the invoices that the user([userId]) is allowed to process.
     */
    @GET("/invoices")
    fun getInvoicesForUser(@Query("userid") userId: String): Single<Response<List<InvoiceRemoteModel>>>

    /**
     * Confirm that the user([userId]) can process the invoice([invoiceId]). The confirmation means that the invoce
     * still exists, it is still not completed and no other user is currently processing it.
     */
    @PUT("/requestAccept")
    @FormUrlEncoded
    fun requestInvoiceForProcessing(
        @Field("userid") userId: String,
        @Field("invoiceid") invoiceId: String
    ): Single<Response<Void>>

    /**
     * Confirm the return of the invoice([invoiceId]). The user([userId]) returns the invoice if, for some reason,
     * he or she does not want to process it anymore. Returning the invoice allows other users to accept it and
     * finish the job.
     */
    @PUT("/requestReturn")
    @FormUrlEncoded
    fun requestInvoiceReturn(
        @Field("userid") userId: String,
        @Field("invoiceid") invoiceId: String
    ): Single<Response<Void>>
}