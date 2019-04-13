package com.evartem.domain.interactor

import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.util.TestDataEntity
import com.evartem.domain.util.TestSchedulers
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations

open class BaseUseCaseTest {
    @Mock
    protected lateinit var invoiceGateway: InvoiceGateway

    protected lateinit var schedulers: Schedulers

    protected lateinit var testData: TestDataEntity

    @Before
    fun baseSetup() {
        MockitoAnnotations.initMocks(this)

        testData = TestDataEntity()
        schedulers = TestSchedulers()
    }
}