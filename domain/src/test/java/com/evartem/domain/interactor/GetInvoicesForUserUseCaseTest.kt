package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGatewayResult
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetInvoicesForUserUseCaseTest : BaseUseCaseTest() {

    private lateinit var useCase: GetInvoicesForUserUseCase

    @Before
    fun setup() {
        useCase = GetInvoicesForUserUseCase(schedulers, invoiceGateway)
    }

    @Test
    fun `Should receive data from the gateway`() {
        // GIVEN a gateway that is ready to emit invoices
        val twoInvoices = listOf(testData.invoice1, testData.invoice2)
        Mockito.`when`(invoiceGateway.getInvoicesForUser(testData.user, false))
            .thenReturn(Observable.just(InvoiceGatewayResult.Invoices(twoInvoices)))

        // WHEN execute the use case
        val testObserver = useCase.execute(Pair(testData.user, false)).test()

        // SHOULD successfully return the two invoices from the gateway
        Mockito.verify(invoiceGateway).getInvoicesForUser(testData.user, false)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceGatewayResult.Invoices(twoInvoices))

        testObserver.dispose()
    }
}