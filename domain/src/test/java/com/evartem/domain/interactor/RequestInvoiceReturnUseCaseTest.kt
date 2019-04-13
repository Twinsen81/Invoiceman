package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class RequestInvoiceReturnUseCaseTest : BaseUseCaseTest() {

    private lateinit var useCase: RequestInvoiceReturnUseCase

    @Before
    fun setup() {
        useCase = RequestInvoiceReturnUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun `Should confirm returning an invoice`() {
        // GIVEN a gateway that is ready to accept returns
        Mockito.`when`(invoiceGateway.requestInvoiceReturn(testData.user, testData.invoice1.id))
            .thenReturn(Observable.just(InvoiceGatewayResult.ReturnConfirmed))

        // WHEN execute the use case
        val testObserver = useCase.execute(Pair(testData.user, testData.invoice1.id)).test()

        // SHOULD successfully return the ReturnConfirmed result
        Mockito.verify(invoiceGateway).requestInvoiceReturn(testData.user, testData.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.ReturnConfirmed)

        testObserver.dispose()
    }
}