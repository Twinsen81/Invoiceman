package com.evartem.invoiceman.invoices.mvi

import com.evartem.domain.entity.auth.User
import com.evartem.domain.gateway.InvoiceGatewayResult
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.invoiceman.invoices.mvi.util.TestDataEntity
import com.evartem.invoiceman.invoices.mvi.util.TestDataUiState
import com.evartem.invoiceman.invoices.mvi.util.TestSchedulers
import com.evartem.invoiceman.invoices.mvi.util.TimberConsoleTree
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

    val testScheduler = TestScheduler()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testDataEntity = TestDataEntity()
        testDataUiState = TestDataUiState()

        Timber.plant(TimberConsoleTree())

        Mockito.`when`(sessionManager.currentUser).thenReturn(User("test@test.com"))

        viewModel = InvoicesViewModel(sessionManager, getInvoicesUseCase, TestSchedulers())
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
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
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

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.emptyList) // show an empty list

        viewModel.addEvent(InvoicesEvent.RefreshScreen)
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

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)
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

    //////////////////////////////////////////
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
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
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

        Timber.d("### Test fun - ${Thread.currentThread()}")

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
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()

            viewModel.uiState
//                .subscribeOn(Schedulers.single())
                .doOnNext { Timber.d("### viewModel.uiState - ${Thread.currentThread()}") }
                .subscribe(testObserverUiState)
            viewModel.uiEffects
//                .observeOn(Schedulers.single())
                .doOnNext { Timber.d("### viewModel.uiEffect - ${Thread.currentThread()}") }
                .subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        // SHOULD
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

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)
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
    fun `Should hide a search view on stopSearch event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)

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
    fun `Should get the query text on Search event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)
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
    fun `Should get the sort property on Sort event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects
            .subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)
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
    fun `Should get an effect on Click event`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(testScheduler)
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState
//            .subscribeOn(Schedulers.single())
            .subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)
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
}