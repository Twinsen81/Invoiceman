package com.evartem.data.gateway

import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.repository.InvoiceRepository
import com.evartem.data.repository.InvoiceRepositoryResult
import com.evartem.data.util.TestDataEntity
import com.evartem.data.util.TestDataLocalModel
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
    fun `Should successfully receive and map a list of invoices from the repository`() {
        // GIVEN a repository that is ready to emit invoices as local model
        val twoInvoicesAsLocal = listOf(testDataLocal.invoice1, testDataLocal.invoice2)
        val twoInvoicesAsEntity = listOf(testDataEntity.invoice1, testDataEntity.invoice2)

        Mockito.`when`(invoiceRepository.getInvoicesForUser(testDataLocal.user.id, false))
            .thenReturn(Observable.just(InvoiceRepositoryResult.Invoices(twoInvoicesAsLocal)))

        // WHEN request data from the repository
        val testObserver = invoiceGateway.getInvoicesForUser(testDataLocal.user, false) .test()

        // SHOULD successfully return the two invoices from the gateway
        Mockito.verify(invoiceRepository).getInvoicesForUser(testDataLocal.user.id, false)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoices(twoInvoicesAsEntity))
    }

    @Test
    fun `Should successfully receive and map an invoice from the repository`() {
        // GIVEN a repository that is ready to emit invoices as local model
        Mockito.`when`(invoiceRepository.getInvoice(testDataLocal.invoice1.id))
            .thenReturn(Observable.just(InvoiceRepositoryResult.Invoice(testDataLocal.invoice1)))

        // WHEN request data from the repository
        val testObserver = invoiceGateway.getInvoice(testDataLocal.invoice1.id) .test()

        // SHOULD successfully return the two invoices from the gateway
        Mockito.verify(invoiceRepository).getInvoice(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoice(testDataEntity.invoice1))
    }
}