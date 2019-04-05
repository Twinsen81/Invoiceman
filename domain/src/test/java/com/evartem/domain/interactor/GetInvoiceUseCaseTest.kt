package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetInvoiceUseCaseTest : BaseUseCaseTest() {

    private lateinit var useCase: GetInvoiceUseCase

    @Before
    fun setup() {
        useCase = GetInvoiceUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun `Should receive data from the gateway`() {
        // GIVEN a gateway that is ready to emit an invoice
        Mockito.`when`(invoiceGateway.getInvoice(testData.invoice1.id))
            .thenReturn(Observable.just(InvoiceGatewayResult.Invoice(testData.invoice1)))

        // WHEN execute the use case
        val testObserver = useCase.execute(testData.invoice1.id).test()

        // SHOULD successfully return the invoice from the gateway
        Mockito.verify(invoiceGateway).getInvoice(testData.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoice(testData.invoice1))
    }
}