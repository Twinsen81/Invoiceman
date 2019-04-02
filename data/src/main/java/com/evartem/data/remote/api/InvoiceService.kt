package com.evartem.data.remote.api

import com.evartem.data.remote.model.InvoiceRemoteModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface InvoiceService {
    @GET("/invoices")
    fun getInvoicesForUser(@Query("userid") userId: String): Single<Response<List<InvoiceRemoteModel>>>

    @POST("/requestAccept")
    fun requestInvoiceForProcessing(
        @Field("userid") userId: String,
        @Field("invoiceid") invoiceId: String
    ): Single<Response<Void>>
}