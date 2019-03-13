package com.evartem.data.gateway.mapper

import com.evartem.data.local.model.InvoiceLocalModel
import com.evartem.data.local.model.ProductLocalModel
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Product
import com.evartem.domain.entity.doc.Result
import com.evartem.domain.entity.doc.ResultStatus
import com.evartem.domain.gateway.InvoiceGatewayResult

class InvoiceMapperToGatewayResult {
    fun toGateway(repoResult: InvoiceRepositoryResult): InvoiceGatewayResult =
        (repoResult as InvoiceRepositoryResult.InvoicesRequestResult).let {
            InvoiceGatewayResult.InvoicesRequestResult(localToEntity(it.invoices), it.response, it.networkError)
        }

    private fun localToEntity(localModel: List<InvoiceLocalModel>) =
        localModel.map { localToEntity(it) }

    private fun localToEntity(localModel: InvoiceLocalModel) =
        Invoice(
            localModel.id,
            localModel.number,
            localModel.date,
            localModel.seller,
            localModel.products.toList().map { productRemoteToLocal(it) },
            localModel.processedByUser,
            localModel.scanCopyUrl,
            localModel.comment
        )

    private fun productRemoteToLocal(localProduct: ProductLocalModel) =
        Product(
            localProduct.id,
            localProduct.article,
            localProduct.description,
            localProduct.quantity,
            localProduct.articleScanRequired,
            localProduct.hasSerialNumber,
            localProduct.serialNumberScanRequired,
            localProduct.equalSerialNumbersAreOk,
            localProduct.results.map {
                Result(
                    if (it.status?.status == 0) ResultStatus.COMPLETED else ResultStatus.FAILED,
                    it.serial,
                    it.comment,
                    it.id
                )
            }.toMutableList(),
            localProduct.serialNumberPattern
        )
}