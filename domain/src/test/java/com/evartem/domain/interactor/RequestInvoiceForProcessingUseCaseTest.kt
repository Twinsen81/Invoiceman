package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RequestInvoiceForProcessingUseCaseTest : BaseUseCaseTest() {

    private lateinit var useCase: RequestInvoiceForProcessingUseCase

    @Before
    fun setup() {
        useCase = RequestInvoiceForProcessingUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun `Should confirm a processing request`() {
        // GIVEN a gateway that is ready to confirm processing
        Mockito.`when`(invoiceGateway.requestInvoiceForProcessing(testData.user, testData.invoice1.id))
            .thenReturn(Observable.just(InvoiceGatewayResult.AcceptConfirmed))

        // WHEN execute the use case
        val testObserver = useCase.execute(Pair(testData.user, testData.invoice1.id)).test()

        // SHOULD successfully return the AcceptConfirmed result
        Mockito.verify(invoiceGateway).requestInvoiceForProcessing(testData.user, testData.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.AcceptConfirmed)

        testObserver.dispose()
    }
}