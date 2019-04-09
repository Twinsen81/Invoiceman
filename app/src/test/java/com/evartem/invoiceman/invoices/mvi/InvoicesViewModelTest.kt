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
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Calls
import org.mockito.internal.verification.Times
import timber.log.Timber

class InvoicesViewModelTest {

    private lateinit var viewModel: InvoicesViewModel

    private lateinit var testDataEntity: TestDataEntity
    private lateinit var testDataUiState: TestDataUiState

    @Mock
    private lateinit var getInvoicesUseCase: GetInvoicesForUserUseCase

    @Mock
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testDataEntity = TestDataEntity()
        testDataUiState = TestDataUiState()

        Timber.plant(TimberConsoleTree())

        Mockito.`when`(sessionManager.currentUser).thenReturn(User("test@test.com"))

        viewModel = InvoicesViewModel(sessionManager, getInvoicesUseCase, TestSchedulers())
    }

    @Test
    fun `Should show loading and invoices upon start`() {
        // GIVEN a use case that emits two invoices
        Mockito.`when`(getInvoicesUseCase.execute(Pair(sessionManager.currentUser, true)))
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(Schedulers.io())
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started)
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

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
                    .subscribeOn(Schedulers.io())
            )
            .thenReturn(
                Observable.just(InvoiceGatewayResult.Invoices(testDataEntity.invoice1And2) as InvoiceGatewayResult)
                    .subscribeOn(Schedulers.io())
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        viewModel.uiState.subscribe(testObserverUiState)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.emptyList) // show an empty list

        viewModel.addEvent(InvoicesEvent.RefreshScreen)

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
                    .subscribeOn(Schedulers.io())
            )

        // WHEN subscribe to the uiState observable (i.e. the UI is started) and send the Refresh event
        val testObserverUiState = TestObserver<InvoicesUiState>()
        val testObserverUiEffect = TestObserver<InvoicesUiEffect>()
        viewModel.uiState.subscribe(testObserverUiState)
        viewModel.uiEffects.subscribe(testObserverUiEffect)

        // SHOULD
        testObserverUiState.awaitCount(2)
            .assertNoErrors()
            .assertValueAt(0, testDataUiState.initialLoading) // show the loading screen
            .assertValueAt(1, testDataUiState.twoInvoices) // display the two invoices from the use case

        viewModel.addEvent(InvoicesEvent.RefreshScreen)

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

    // When resubscribing -> emit the last state without calling the usecase
    // Render error from usecase effect
    // Render search events
    // Render sort event
    // Render click events
    /*InvoicesEvent.LoadScreen -> onRefreshData(event.reloadFromServer)
    is InvoicesEvent.RefreshScreen -> Observable.merge(relay(event), onRefreshData())
    is InvoicesEvent.Click -> onInvoiceClicked(event)
    is InvoicesEvent.Search,
    is InvoicesEvent.Sort,
    is InvoicesEvent.Empty*/
}