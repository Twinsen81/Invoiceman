package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.invoices.mvi.util.TestDataEntity
import com.evartem.invoiceman.invoices.mvi.util.TestDataUiState
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import timber.log.Timber
import java.util.concurrent.TimeUnit

class InvoicesViewModelTest {

    private lateinit var viewModel: InvoicesViewModel

    private lateinit var testDataEntity: TestDataEntity
    private lateinit var testDataUiState: TestDataUiState

    @Mock
    private lateinit var getInvoicesUseCase: GetInvoicesForUserUseCase

    @Mock
    private lateinit var sessionManager: SessionManager

    private val testScheduler = TestScheduler()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testDataEntity = TestDataEntity()
        testDataUiState = TestDataUiState()

        // Timber.plant(TimberConsoleTree())

        Mockito.`when`(sessionManager.currentUser).thenReturn(User("test@test.com"))

        viewModel = InvoicesViewModel(sessionManager, getInvoicesUseCase)
    }

    @After
    fun tearDown() {
        Timber.uprootAll()
    }

    @Test
    fun `Should show loading and invoices upon start`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        testObserverUiState.dispose()
    }

    @Test
    fun `Should update data upon refresh event`() {
        // GIVEN a use case that at first call emits no invoices and at second - two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(listOf()) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event (1)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.emptyList) // show an empty list

        /* (1) */ viewModel.addEvent(InvoicesEvent.RefreshScreen)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserverUiState.awaitCount(4)
            .assertNoErrors()
            .assertValueAt(2, testDataUiState.emptyListRefreshing) // show the refreshing screen
            .assertValueAt(3, testDataUiState.twoInvoices) // display the two invoices from the use case

        testObserverUiState.dispose()
    }

    @Test
    fun `Should display an effect if no new data upon refresh`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event (1)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        /* (1) */ viewModel.addEvent(InvoicesEvent.RefreshScreen)
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        testObserverUiState.awaitCount(4)
            .assertNoErrors()
            .assertValueAt(2, testDataUiState.twoInvoicesRefreshing) // show the refreshing screen
            .assertValueAt(3, testDataUiState.twoInvoices) // display the two invoices from the use case

        // and display the "no new data" effect
        testObserverUiEffect.awaitCount(1)
            .assertNoErrors()
            .assertValue(InvoicesUiEffect.NoNewData())

        Mockito.verify(getInvoicesUseCase, Times(2)).execute(Pair(sessionManager.currentUser, true))

        testObserverUiState.dispose()
        testObserverUiEffect.dispose()
    }

    @Test
    fun `Should display an effect if error returned from use case`() {
        // GIVEN a use case that emits an error
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Error(testDataEntity.gatewayError) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.emptyList) // display an empty list

        // and display the error effect
        testObserverUiEffect.awaitCount(1)
            .assertNoErrors()
            .assertValue(InvoicesUiEffect.Error(testDataEntity.gatewayError))

        testObserverUiState.dispose()
        testObserverUiEffect.dispose()
    }

    @Test
    fun `Should display data and an effect if error and data returned from use case`() {
        // GIVEN a use case that emits two invoices along with an error
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(
                    InvoiceGatewayResult.Invoices(
                        testDataEntity.invoice1And2, testDataEntity.gatewayError
                    ) as InvoiceGatewayResult
                )
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        // and display the error effect
        testObserverUiEffect.awaitCount(1)
            .assertNoErrors()
            .assertValue(InvoicesUiEffect.Error(testDataEntity.gatewayError))

        testObserverUiState.dispose()
        testObserverUiEffect.dispose()
    }

    @Test
    fun `Should display a search view on startSearch event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the start search event (1)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        /* (1) */ viewModel.addEvent(InvoicesEvent.Search(startSearch = true))

        testObserverUiState.awaitCount(3)
            .assertNoErrors()
            .assertValueAt(2, testDataUiState.twoInvoicesStartSearch) // display the search view

        Mockito.verify(getInvoicesUseCase, Times(1)).execute(Pair(sessionManager.currentUser, true))

        testObserverUiState.dispose()
    }

    @Test
    fun `Should hide a search view on stopSearch event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and
        // send the start search event (1) and send the stop search event (2)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        /* (1) */ viewModel.addEvent(InvoicesEvent.Search(startSearch = true))

        testObserverUiState.awaitCount(3)
            .assertNoErrors()
            .assertValueAt(2, testDataUiState.twoInvoicesStartSearch) // display the search view

        /* (2) */ viewModel.addEvent(InvoicesEvent.Search(stopSearch = true))

        testObserverUiState.awaitCount(4)
            .assertNoErrors()
            .assertValueAt(3, testDataUiState.twoInvoices) // hide the search view

        Mockito.verify(getInvoicesUseCase, Times(1)).execute(Pair(sessionManager.currentUser, true))

        testObserverUiState.dispose()
    }

    @Test
    fun `Should get the query text on Search event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and
        // send the start search event (1) and send the search request event (2)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        /* (1) */ viewModel.addEvent(InvoicesEvent.Search(startSearch = true))

        testObserverUiState.awaitCount(3)
            .assertNoErrors()
            .assertValueAt(2, testDataUiState.twoInvoicesStartSearch) // display the search view

        /* (2) */ viewModel.addEvent(InvoicesEvent.Search(searchQuery = "test"))

        testObserverUiState.awaitCount(4)
            .assertNoErrors()
            .assertValueAt(3, testDataUiState.twoInvoicesSearchQuery) // hide the search view

        Mockito.verify(getInvoicesUseCase, Times(1)).execute(Pair(sessionManager.currentUser, true))

        testObserverUiState.dispose()
    }

    @Test
    fun `Should get the sort property on Sort event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the sort event (1)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        /* (1) */ viewModel.addEvent(InvoicesEvent.Sort(InvoicesEvent.Sort.SortBy.NUMBER))

        testObserverUiState.awaitCount(3)
            .assertNoErrors()
            .assertValueAt(2, testDataUiState.twoInvoicesSortByNumber) // display the list sorted by a field

        Mockito.verify(getInvoicesUseCase, Times(1)).execute(Pair(sessionManager.currentUser, true))

        testObserverUiState.dispose()
    }

    @Test
    fun `Should get an effect on Click event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the click event (1)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        // SHOULD
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        /* (1) */ viewModel.addEvent(InvoicesEvent.Click(testDataEntity.invoice1.id))

        // and receive the click effect with proper invoice ID
        testObserverUiEffect.awaitCount(1)
            .assertNoErrors()
            .assertValue(InvoicesUiEffect.InvoiceClick(testDataEntity.invoice1.id))

        Mockito.verify(getInvoicesUseCase, Times(1)).execute(Pair(sessionManager.currentUser, true))

        testObserverUiState.dispose()
        testObserverUiEffect.dispose()
    }
}