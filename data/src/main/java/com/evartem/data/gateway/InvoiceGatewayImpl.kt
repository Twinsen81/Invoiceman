package com.evartem.data.gateway

import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.repository.InvoiceRepository
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.domain.entity.auth.User
import com.evartem.domain.entity.doc.Invoice
import com.evartem.domain.entity.doc.Result
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable

/**
 * An [InvoiceGateway] interface implementation that just forwards all requests to repository and maps the
 * return values from [InvoiceRepositoryResult] to [InvoiceGatewayResult].
 *
 * @property invoiceRepository the repository instance to do the actual work.
 * @property invoiceMapper the mapper for repository -> entity data mapping
 */
class InvoiceGatewayImpl(val invoiceRepository: InvoiceRepository, val invoiceMapper: InvoiceMapperToGatewayResult) :
    InvoiceGateway {

    override fun getInvoicesForUser(user: User, refresh: Boolean): Observable<InvoiceGatewayResult> =
        invoiceRepository.getInvoicesForUser(user.id, refresh)
            .map { invoiceMapper.toGateway(it) }

    override fun getInvoice(invoiceId: String): Observable<InvoiceGatewayResult> =
        invoiceRepository.getInvoice(invoiceId)
            .map { invoiceMapper.toGateway(it) }

    override fun getProduct(invoiceId: String, productId: Int): Observable<InvoiceGatewayResult> =
        invoiceRepository.getProduct(invoiceId, productId)
            .map { invoiceMapper.toGateway(it) }

    override fun requestInvoiceForProcessing(user: User, invoiceId: String): Observable<InvoiceGatewayResult> =
        invoiceRepository.requestInvoiceForProcessing(user, invoiceId)
            .map { invoiceMapper.toGateway(it) }

    override fun requestInvoiceReturn(user: User, invoiceId: String): Observable<InvoiceGatewayResult> =
        invoiceRepository.requestInvoiceReturn(user, invoiceId)
            .map { invoiceMapper.toGateway(it) }

    override fun insertOrUpdateResult(
        invoiceId: String,
        productId: Int,
        result: Result
    ): Observable<InvoiceGatewayResult> =
        invoiceRepository.insertOrUpdateResult(invoiceId, productId, invoiceMapper.entityToLocal(result))
            .map { invoiceMapper.toGateway(it) }

    override fun deleteResult(invoiceId: String, productId: Int, resultId: Int): Observable<InvoiceGatewayResult> =
        invoiceRepository.deleteResult(invoiceId, productId, resultId)
            .map { invoiceMapper.toGateway(it) }

    override fun submitInvoicesWithResults(user: User, invoices: List<Invoice>): Observable<InvoiceGatewayResult> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}