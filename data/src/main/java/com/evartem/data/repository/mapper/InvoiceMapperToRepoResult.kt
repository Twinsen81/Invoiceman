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

class InvoiceMapperToRepoResult {

    val emptyResult
        get() = InvoiceRepositoryResult.InvoicesRequestResult(listOf(), true)

    fun localToResult(localModel: List<InvoiceLocalModel>): InvoiceRepositoryResult =
        InvoiceRepositoryResult.InvoicesRequestResult(localModel, true)

    fun localToResult(localModel: InvoiceLocalModel): InvoiceRepositoryResult =
        InvoiceRepositoryResult.InvoiceRequestResult(localModel, true)

    fun remoteToResult(remoteResponse: Response<List<InvoiceRemoteModel>>): InvoiceRepositoryResult {
        if (remoteResponse.isSuccessful && remoteResponse.body() != null)
            return InvoiceRepositoryResult.InvoicesRequestResult(invoiceRemoteToLocal(remoteResponse.body()!!), true)

        return createNetworkErrorResult(
            GatewayError.ErrorCode.getByValue(remoteResponse.code()),
            remoteResponse.raw().body()?.string() ?: remoteResponse.message()
        )
    }

    fun fromException(exception: Throwable): InvoiceRepositoryResult =
        when (exception) {
            is JsonEncodingException, is EOFException -> createNetworkErrorResult(
                GatewayError.ErrorCode.WRONG_SERVER_RESPONSE, exception.message, exception
            )
            else -> createNetworkErrorResult(
                GatewayError.ErrorCode.GENERAL_NETWORK_ERROR, exception.message, exception
            )
        }

    private fun createNetworkErrorResult(code: GatewayErrorCode, message: String?, exception: Throwable? = null) =
        InvoiceRepositoryResult.InvoicesRequestResult(listOf(), false, GatewayError(code, message, exception))

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