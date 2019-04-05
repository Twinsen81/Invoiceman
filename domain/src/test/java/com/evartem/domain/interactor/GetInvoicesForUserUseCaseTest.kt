package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.util.TestData.invoice1
import com.evartem.domain.util.TestData.invoice2
import com.evartem.domain.util.TestData.user
import com.evartem.domain.util.TestSchedulers
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class GetInvoicesForUserUseCaseTest {

    @Mock
    private lateinit var invoiceGateway: InvoiceGateway

    private lateinit var schedulers: Schedulers
    private lateinit var getInvoicesForUserUseCase: GetInvoicesForUserUseCase

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        schedulers = TestSchedulers()
        getInvoicesForUserUseCase = GetInvoicesForUserUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun shouldSuccessfullyReceiveDataFromTheGateway() {
        // GIVEN a gateway that is ready to emit invoices
        val twoInvoices = listOf(invoice1, invoice2)
        Mockito.`when`(invoiceGateway.getInvoicesForUser(user, false))
            .thenReturn(Observable.just(InvoiceGatewayResult.Invoices(twoInvoices)))

        // WHEN execute the use case
        val testObserver = getInvoicesForUserUseCase.execute(Pair(user, false)).test()

        // SHOULD successfully return the two invoices from the gateway
        Mockito.verify(invoiceGateway).getInvoicesForUser(user, false)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoices(twoInvoices))
    }
}