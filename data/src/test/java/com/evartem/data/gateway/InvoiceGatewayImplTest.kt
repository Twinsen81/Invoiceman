package com.evartem.data.gateway

import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.repository.InvoiceRepository
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.data.util.TestDataEntity
import com.evartem.data.util.TestDataLocalModel
import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.GatewayError
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class InvoiceGatewayImplTest {

    @Mock
    private lateinit var invoiceRepository: InvoiceRepository

    private lateinit var invoiceMapper: InvoiceMapperToGatewayResult

    private lateinit var invoiceGateway: InvoiceGateway

    private lateinit var testDataLocal: TestDataLocalModel
    private lateinit var testDataEntity: TestDataEntity

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testDataLocal = TestDataLocalModel()
        testDataEntity = TestDataEntity()
        invoiceMapper = InvoiceMapperToGatewayResult()
        invoiceGateway = InvoiceGatewayImpl(invoiceRepository, invoiceMapper)
    }

    @Test
    fun `Should receive and map a list of invoices from the repository`() {
        // GIVEN a repository that is ready to emit invoices as local model
        val twoInvoicesAsLocal = listOf(testDataLocal.invoice1, testDataLocal.invoice2)
        val twoInvoicesAsEntity = listOf(testDataEntity.invoice1, testDataEntity.invoice2)

        Mockito.`when`(invoiceRepository.getInvoicesForUser(testDataLocal.user.id, false))
            .thenReturn(Observable.just(InvoiceRepositoryResult.Invoices(twoInvoicesAsLocal)))

        // WHEN request data from the repository
        val testObserver = invoiceGateway.getInvoicesForUser(testDataLocal.user, false).test()

        // SHOULD successfully return the two invoices from the gateway as entities
        Mockito.verify(invoiceRepository).getInvoicesForUser(testDataLocal.user.id, false)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoices(twoInvoicesAsEntity))
    }

    @Test
    fun `Should receive and map an invoice`() {
        // GIVEN a repository that is ready to emit an invoice as local model
        Mockito.`when`(invoiceRepository.getInvoice(testDataLocal.invoice1.id))
            .thenReturn(Observable.just(InvoiceRepositoryResult.Invoice(testDataLocal.invoice1)))

        // WHEN request data from the repository
        val testObserver = invoiceGateway.getInvoice(testDataLocal.invoice1.id).test()

        // SHOULD successfully return the invoice from the gateway as an entity
        Mockito.verify(invoiceRepository).getInvoice(testDataLocal.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoice(testDataEntity.invoice1))
    }

    @Test
    fun `Should receive and map the result of a processing request`() {
        // GIVEN a repository that is ready to accept processing requests and emits results as InvoiceRepositoryResult
        Mockito.`when`(invoiceRepository.requestInvoiceForProcessing(testDataLocal.user, testDataLocal.invoice1.id))
            .thenReturn(Observable.just(InvoiceRepositoryResult.AcceptConfirmed))

        // WHEN request processing an invoice
        val testObserver =
            invoiceGateway.requestInvoiceForProcessing(testDataLocal.user, testDataLocal.invoice1.id).test()

        // SHOULD successfully return the result from the gateway as InvoiceGatewayResult
        Mockito.verify(invoiceRepository).requestInvoiceForProcessing(testDataLocal.user, testDataLocal.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.AcceptConfirmed)
    }

    @Test
    fun `Should receive and map the result of a return request`() {
        // GIVEN a repository that is ready for returning requests and emits results as InvoiceRepositoryResult
        Mockito.`when`(invoiceRepository.requestInvoiceReturn(testDataLocal.user, testDataLocal.invoice1.id))
            .thenReturn(Observable.just(InvoiceRepositoryResult.ReturnConfirmed))

        // WHEN request returning of an invoice
        val testObserver =
            invoiceGateway.requestInvoiceReturn(testDataLocal.user, testDataLocal.invoice1.id).test()

        // SHOULD successfully return the result from the gateway as InvoiceGatewayResult
        Mockito.verify(invoiceRepository).requestInvoiceReturn(testDataLocal.user, testDataLocal.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.ReturnConfirmed)
    }

    @Test
    fun `Should receive and map an error`() {
        // GIVEN a repository that emits an error when requested for invoices
        Mockito.`when`(invoiceRepository.getInvoicesForUser(testDataLocal.user.id, false))
            .thenReturn(Observable.just(InvoiceRepositoryResult.Error(testDataLocal.error)))

        // WHEN request data from the repository
        val testObserver = invoiceGateway.getInvoicesForUser(testDataLocal.user, false).test()

        // SHOULD successfully return the result from the gateway as InvoiceGatewayResult
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Error(testDataLocal.error))
    }
}