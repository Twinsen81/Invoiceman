package com.evartem.data.repository.mapper

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.local.model.ResultLocalModel
import com.evartem.data.local.model.ResultStatusLocalModel
import com.evartem.data.remote.model.InvoiceRemoteModel
import com.evartem.data.remote.model.ProductRemoteModel
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.realm.RealmList
import retrofit2.Response

class InvoiceMapperToRepoResult {

    fun localToResult(localModel: List<InvoiceLocalModel>): InvoiceRepositoryResult =
        InvoiceRepositoryResult.InvoicesRequestResult(localModel, InvoiceGatewayResult.ResponseCode.SUCCESS)

    fun remoteToResult(remoteResponse: Response<List<InvoiceRemoteModel>>): InvoiceRepositoryResult {

        if (remoteResponse.isSuccessful) {
            return if (remoteResponse.body() != null)
                InvoiceRepositoryResult.InvoicesRequestResult(
                    invoiceRemoteToLocal(remoteResponse.body()!!),
                    InvoiceGatewayResult.ResponseCode.SUCCESS
                )
            else
                createNetworkErrorResult(0, "Empty response from the server")
        }

        return createNetworkErrorResult(remoteResponse.code(), remoteResponse.message())
    }

    private fun createNetworkErrorResult(code: Int, message: String) =
        InvoiceRepositoryResult.InvoicesRequestResult(
            listOf(),
            InvoiceGatewayResult.ResponseCode.DENIED_NETWORK_ERROR,
            InvoiceGatewayResult.NetworkError(code, message)
        )

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