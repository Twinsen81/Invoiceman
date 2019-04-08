package com.evartem.data.remote.api

import com.evartem.data.remote.model.InvoiceRemoteModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface InvoiceService {
    @GET("/invoices")
    fun getInvoicesForUser(@Query("userid") userId: String): Single<Response<List<InvoiceRemoteModel>>>

    @PUT("/requestAccept")
    @FormUrlEncoded
    fun requestInvoiceForProcessing(
        @Field("userid") userId: String,
        @Field("invoiceid") invoiceId: String
    ): Single<Response<Void>>

    @PUT("/requestReturn")
    @FormUrlEncoded
    fun requestInvoiceReturn(
        @Field("userid") userId: String,
        @Field("invoiceid") invoiceId: String
    ): Single<Response<Void>>
}