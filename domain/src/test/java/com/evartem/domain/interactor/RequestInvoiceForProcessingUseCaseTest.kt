package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.util.TestData
import com.evartem.domain.util.TestData.user
import com.evartem.domain.util.TestSchedulers
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class RequestInvoiceForProcessingUseCaseTest {

    @Mock
    private lateinit var invoiceGateway: InvoiceGateway

    private lateinit var schedulers: Schedulers
    private lateinit var requestProcessingUseCase: RequestInvoiceForProcessingUseCase

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        schedulers = TestSchedulers()
        requestProcessingUseCase = RequestInvoiceForProcessingUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun shouldSuccessfullyRequestProcessingFromTheGateway() {
        // GIVEN a gateway that is ready to confirm processing
        Mockito.`when`(invoiceGateway.requestInvoiceForProcessing(user, TestData.invoice1.id))
            .thenReturn(Observable.just(InvoiceGatewayResult.AcceptConfirmed))

        // WHEN execute the use case
        val testObserver = requestProcessingUseCase.execute(Pair(user, TestData.invoice1.id)).test()

        // SHOULD successfully return the AcceptConfirmed result
        Mockito.verify(invoiceGateway).requestInvoiceForProcessing(user, TestData.invoice1.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.AcceptConfirmed)
    }
}