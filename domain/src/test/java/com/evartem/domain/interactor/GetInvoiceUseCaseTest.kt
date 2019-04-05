package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.util.TestData
import com.evartem.domain.util.TestSchedulers
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetInvoiceUseCaseTest {
    @Mock
    private lateinit var invoiceGateway: InvoiceGateway

    private lateinit var schedulers: Schedulers
    private lateinit var getInvoiceUseCase: GetInvoiceUseCase

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        schedulers = TestSchedulers()
        getInvoiceUseCase = GetInvoiceUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun shouldSuccessfullyReceiveDataFromTheGateway() {
        // GIVEN a gateway that is ready to emit an invoice
        Mockito.`when`(invoiceGateway.getInvoice(TestData.invoice1.id))
            .thenReturn(Observable.just(InvoiceGatewayResult.Invoice(TestData.invoice1)))

        // WHEN execute the use case
        val testObserver = getInvoiceUseCase.execute(TestData.invoice1.id).test()

        // SHOULD successfully return the invoice from the gateway
        Mockito.verify(invoiceGateway).getInvoice(TestData.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoice(TestData.invoice1))
    }
}