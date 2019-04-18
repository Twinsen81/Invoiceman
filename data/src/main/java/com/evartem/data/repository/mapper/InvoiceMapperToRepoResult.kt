package com.evartem.data.repository.mapper

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.local.model.ResultStatusLocalModel
import com.evartem.data.remote.model.InvoiceRemoteModel
import com.evartem.data.remote.model.ProductRemoteModel
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.GatewayErrorCode
import com.squareup.moshi.JsonEncodingException
import io.realm.RealmList
import retrofit2.Response
import java.io.EOFException

/**
 * Maps DTOs between local and remote models, converting responses from Retrofit to
 * the corresponding [InvoiceRepositoryResult] objects.
 * Unwraps the Retrofit's [Response] and maps the response code to the corresponding [GatewayErrorCode].
 */
class InvoiceMapperToRepoResult {

    fun remoteAcceptToResult(remoteResponse: Response<Void>): InvoiceRepositoryResult {

        if (remoteResponse.isSuccessful)
            return InvoiceRepositoryResult.AcceptConfirmed

        return errorFromResponse(remoteResponse)
    }

    fun remoteReturnToResult(remoteResponse: Response<Void>): InvoiceRepositoryResult {

        if (remoteResponse.isSuccessful)
            return InvoiceRepositoryResult.ReturnConfirmed

        return errorFromResponse(remoteResponse)
    }

    fun remoteToResult(remoteResponse: Response<List<InvoiceRemoteModel>>): InvoiceRepositoryResult {

        if (remoteResponse.isSuccessful && remoteResponse.body() != null)
            return InvoiceRepositoryResult.Invoices(invoiceRemoteToLocal(remoteResponse.body()!!))

        return errorFromResponse(remoteResponse)
    }

    /**
     * Create an [InvoiceRepositoryResult.Error] if an exception was thrown somewhere on the way of processing
     * the server's response.
     */
    fun errorResultFromException(exception: Throwable): InvoiceRepositoryResult =
        when (exception) {
            is JsonEncodingException, is EOFException -> createErrorResult(
                GatewayError.ErrorCode.WRONG_SERVER_RESPONSE, exception.message, exception
            )
            else -> createErrorResult(
                GatewayError.ErrorCode.GENERAL_ERROR, exception.message, exception
            )
        }

    fun emptyListFromException(exception: Throwable) =
        InvoiceRepositoryResult.Invoices(
            listOf(),
            GatewayError(GatewayError.ErrorCode.GENERAL_ERROR, exception.message, exception)
        )

    /**
     * Create an [InvoiceRepositoryResult.Error] object if [Response.isSuccessful] = false.
     */
    private fun <T> errorFromResponse(response: Response<T>) =
        createErrorResult(
            GatewayError.ErrorCode.getByValue(response.code()),
            try {
                response.raw().body()?.string() ?: response.message()
            } catch (ignored: Exception) {
                response.message()
            }
        )

    private fun createErrorResult(code: GatewayErrorCode, message: String?, exception: Throwable? = null) =
        InvoiceRepositoryResult.Error(GatewayError(code, message, exception))

    private fun invoiceRemoteToLocal(remoteModel: List<InvoiceRemoteModel>) =
        remoteModel.map { invoiceRemoteToLocal(it) }

    private fun invoiceRemoteToLocal(remoteModel: InvoiceRemoteModel) =
        InvoiceLocalModel(
            remoteModel.id,
            remoteModel.number,
            remoteModel.date,
            remoteModel.seller,
            RealmList(*remoteModel.products.map { productRemoteToLocal(it) }.toTypedArray()),
            remoteModel.processedByUser,
            remoteModel.scanCopyUrl,
            remoteModel.comment
        )

    private fun productRemoteToLocal(remoteProduct: ProductRemoteModel) =
        ProductLocalModel(
            remoteProduct.id,
            remoteProduct.article,
            remoteProduct.description,
            remoteProduct.quantity,
            remoteProduct.articleScanRequired,
            remoteProduct.hasSerialNumber,
            remoteProduct.serialNumberScanRequired,
            remoteProduct.equalSerialNumbersAreOk,
            RealmList(*remoteProduct.results.map {
                ResultLocalModel(
                    it.id,
                    ResultStatusLocalModel(it.status),
                    it.serial,
                    it.comment
                )
            }.toTypedArray()),
            remoteProduct.serialNumberPattern
        )
}