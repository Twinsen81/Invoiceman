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

class RequestInvoiceReturnUseCaseTest {

    @Mock
    private lateinit var invoiceGateway: InvoiceGateway

    private lateinit var schedulers: Schedulers
    private lateinit var requestReturnUseCase: RequestInvoiceReturnUseCase

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        schedulers = TestSchedulers()
        requestReturnUseCase = RequestInvoiceReturnUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun shouldSuccessfullyReturnInvoice() {
        // GIVEN a gateway that is ready to accept returns
        Mockito.`when`(invoiceGateway.requestInvoiceReturn(TestData.user, TestData.invoice1.id))
            .thenReturn(Observable.just(InvoiceGatewayResult.ReturnConfirmed))

        // WHEN execute the use case
        val testObserver = requestReturnUseCase.execute(Pair(TestData.user, TestData.invoice1.id)).test()

        // SHOULD successfully return the ReturnConfirmed result
        Mockito.verify(invoiceGateway).requestInvoiceReturn(TestData.user, TestData.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.ReturnConfirmed)
    }
}