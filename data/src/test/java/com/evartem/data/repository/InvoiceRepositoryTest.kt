package com.evartem.data.repository

import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.remote.model.InvoiceRemoteModel
import com.evartem.data.repository.mapper.InvoiceMapperToRepoResult
import com.evartem.data.util.TestDataLocalModel
import com.evartem.data.util.TestDataRemoteModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import retrofit2.Response

class InvoiceRepositoryTest {

    @Mock
    private lateinit var localDataSource: InvoiceLocalDataSource
    @Mock
    private lateinit var remoteDataSource: InvoiceService

    private lateinit var testDataLocal: TestDataLocalModel
    private lateinit var testDataRemote: TestDataRemoteModel

    private lateinit var repository: InvoiceRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        testDataRemote = TestDataRemoteModel()
        testDataLocal = TestDataLocalModel()

        repository = InvoiceRepository(localDataSource, remoteDataSource, InvoiceMapperToRepoResult())
    }

    @Test
    fun `Given an empty local & non-empty remote, should fetch data from remote and insert into local`() {
        // GIVEN a remote data source with two invoices
        Mockito.`when`(remoteDataSource.getInvoicesForUser(testDataLocal.user.id))
            .thenReturn(Single.just(Response.success<List<InvoiceRemoteModel>>(testDataRemote.invoice1And2)))

        // and an empty local data source
        Mockito.`when`(localDataSource.getInvoices()).thenReturn(Single.just(listOf()))
        Mockito.`when`(localDataSource.isEmpty).thenReturn(true)

        // WHEN request invoices from the repository
        val testObserver =
            repository.getInvoicesForUser(testDataLocal.user.id, false).test()

        // SHOULD return the two invoices
        Mockito.verify(localDataSource).getInvoices()
        Mockito.verify(remoteDataSource).getInvoicesForUser(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceRepositoryResult.Invoices(testDataLocal.invoice1And2))
        // and insert them into the local data source
        Mockito.verify(localDataSource).deleteAllAndInsert(testDataLocal.invoice1And2)
    }

    @Test
    fun `Given a non-empty local & remote, should fetch data only from local if refresh=false`() {
        // GIVEN a remote data source with two invoices
        Mockito.`when`(remoteDataSource.getInvoicesForUser(testDataLocal.user.id))
            .thenReturn(Single.just(Response.success<List<InvoiceRemoteModel>>(testDataRemote.invoice1And2)))

        // and a non-empty local data source
        Mockito.`when`(localDataSource.getInvoices()).thenReturn(Single.just(listOf(testDataLocal.invoice1)))
        Mockito.`when`(localDataSource.isEmpty).thenReturn(false)

        // WHEN request invoices from the repository with refresh=false
        val testObserver =
            repository.getInvoicesForUser(testDataLocal.user.id, false).test()

        // SHOULD return only the invoice stored in the local data source
        Mockito.verify(localDataSource).getInvoices()
        Mockito.verify(remoteDataSource, never()).getInvoicesForUser(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceRepositoryResult.Invoices(listOf(testDataLocal.invoice1)))
    }

    @Test
    fun `Given a non-empty local & remote, should join data from local & remote if refresh=true`() {
        // GIVEN a remote data source with an invoice that is not present in the local data source
        Mockito.`when`(remoteDataSource.getInvoicesForUser(testDataLocal.user.id))
            .thenReturn(Single.just(Response.success<List<InvoiceRemoteModel>>(listOf(testDataRemote.invoice2))))

        // and a non-empty local data source
        Mockito.`when`(localDataSource.getInvoices()).thenReturn(Single.just(listOf(testDataLocal.invoice1)))
        Mockito.`when`(localDataSource.isEmpty).thenReturn(false)

        // WHEN request invoices from the repository with refresh=true
        val testObserver =
            repository.getInvoicesForUser(testDataLocal.user.id, true).test()

        // SHOULD return only the invoice stored in the local data source
        Mockito.verify(localDataSource).getInvoices()
        Mockito.verify(remoteDataSource).getInvoicesForUser(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceRepositoryResult.Invoices(testDataLocal.invoice1And2))
    }

    @Test
    fun `Given the same invoice in local & remote, should update in local if not being processed`() {
        // GIVEN a remote data source with an invoice
        Mockito.`when`(remoteDataSource.getInvoicesForUser(testDataLocal.user.id))
            .thenReturn(Single.just(Response.success<List<InvoiceRemoteModel>>(listOf(testDataRemote.invoice1Updated))))

        // and a non-empty local data source with the same invoice ID but different data and the invoice is
        // not being processed by the user
        Mockito.`when`(localDataSource.getInvoices()).thenReturn(Single.just(listOf(testDataLocal.invoice1)))
        Mockito.`when`(localDataSource.isEmpty).thenReturn(false)

        // WHEN request invoices from the repository with refresh=true
        val testObserver =
            repository.getInvoicesForUser(testDataLocal.user.id, true).test()

        // SHOULD return the updated invoice
        Mockito.verify(localDataSource).getInvoices()
        Mockito.verify(remoteDataSource).getInvoicesForUser(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceRepositoryResult.Invoices(listOf(testDataLocal.invoice1Updated)))
        // and insert it into the local data source
        Mockito.verify(localDataSource).deleteAllAndInsert(listOf(testDataLocal.invoice1Updated))
    }

    @Test
    fun `Given the same invoice in local & remote, should not update in local if being processed`() {
        // GIVEN a remote data source with an invoice
        Mockito.`when`(remoteDataSource.getInvoicesForUser(testDataLocal.user.id))
            .thenReturn(Single.just(Response.success<List<InvoiceRemoteModel>>(listOf(testDataRemote.invoice1Updated))))

        // and a non-empty local data source with the same invoice ID but different data and the invoice is
        // being processed by the user
        Mockito.`when`(localDataSource.getInvoices()).thenReturn(Single.just(listOf(testDataLocal.invoice1BeingProcessed)))
        Mockito.`when`(localDataSource.isEmpty).thenReturn(false)

        // WHEN request invoices from the repository with refresh=true
        val testObserver =
            repository.getInvoicesForUser(testDataLocal.user.id, true).test()

        // SHOULD return the invoice in the local data source without updating it from the remote
        Mockito.verify(localDataSource).getInvoices()
        Mockito.verify(remoteDataSource).getInvoicesForUser(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue(InvoiceRepositoryResult.Invoices(listOf(testDataLocal.invoice1BeingProcessed)))
    }

    @Test
    fun `Given a non-empty local, when exception is thrown in remote, should return local and error info`() {
        // GIVEN a remote data source that throws an exception when called
        Mockito.`when`(remoteDataSource.getInvoicesForUser(testDataLocal.user.id))
            .thenThrow(RuntimeException("Test exception"))

        // and a non-empty local data source
        Mockito.`when`(localDataSource.getInvoices()).thenReturn(Single.just(testDataLocal.invoice1And2))
        Mockito.`when`(localDataSource.isEmpty).thenReturn(false)

        // WHEN request invoices from the repository with refresh=true
        val testObserver =
            repository.getInvoicesForUser(testDataLocal.user.id, true).test()

        // SHOULD return the data from the local data source and the info about the exception
        Mockito.verify(localDataSource).getInvoices()
        Mockito.verify(remoteDataSource).getInvoicesForUser(testDataLocal.user.id)
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue { it is InvoiceRepositoryResult.Invoices && it.invoices == testDataLocal.invoice1And2 }
            .assertValue {
                it is InvoiceRepositoryResult.Invoices &&
                        it.gatewayError?.exception?.message == "Test exception"
            }
    }
}