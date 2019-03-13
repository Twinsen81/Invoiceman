package com.evartem.data.repository.mapper

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.remote.model.InvoiceRemoteModel
import com.evartem.data.repository.InvoiceRepositoryResult
import retrofit2.Response

class InvoiceMapperToResult {

    fun localToResult(localModel: List<InvoiceLocalModel>) =
        InvoiceRepositoryResult.InvoicesRequestResult(localModel, InvoiceRepositoryResult.ResponseCode.SUCCESS)

    fun remoteToResult(remoteResponse: Response<List<InvoiceRemoteModel>>): InvoiceRepositoryResult {

        if (remoteResponse.isSuccessful) {
            return if (remoteResponse.body() != null)
                InvoiceRepositoryResult.InvoicesRequestResult(
                    remoteToLocal(remoteResponse.body()!!),
                    InvoiceRepositoryResult.ResponseCode.SUCCESS
                )
            else
                createNetworkErrorResult(0, "Empty response from the server")
        }

        return createNetworkErrorResult(remoteResponse.code(), remoteResponse.message())
    }

    private fun remoteToLocal(remoteModel: InvoiceRemoteModel): InvoiceLocalModel {
    }

    private fun remoteToLocal(remoteModel: List<InvoiceRemoteModel>) =
        remoteModel.map { remoteToLocal(it) }

    private fun createNetworkErrorResult(code: Int, message: String) =
        InvoiceRepositoryResult.InvoicesRequestResult(
            listOf(),
            InvoiceRepositoryResult.ResponseCode.DENIED_NETWORK_ERROR,
            InvoiceRepositoryResult.NetworkError(code, message)
        )
}