package com.evartem.invoiceman.di

import com.evartem.backendsim.InvoiceBackendSimulation
import com.evartem.data.gateway.InvoiceGatewayImpl
import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.remote.api.createInvoiceNetworkClient
import com.evartem.data.repository.InvoiceRepository
import com.evartem.data.repository.mapper.InvoiceMapperToRepoResult
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.interactor.*
import com.evartem.invoiceman.BuildConfig
import com.evartem.invoiceman.invoice.mvi.InvoiceDetailViewModel
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModel
import com.evartem.invoiceman.util.DEMO_USER
import com.evartem.invoiceman.util.SessionManager
import io.reactivex.Scheduler
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule: Module = module {
    viewModel { InvoicesViewModel(sessionManager = get(), getInvoicesForUserUseCase = get()) }
    viewModel {
        InvoiceDetailViewModel(
            sessionManager = get(),
            getInvoiceUseCase = get(),
            requestProcessingUseCase = get(),
            requestReturnUseCase = get()
        )
    }
}

val useCasesModule: Module = module {
    factory { GetInvoicesForUserUseCase(schedulers = get(), gateway = get()) }
    factory { GetInvoiceUseCase(schedulers = get(), gateway = get()) }
    factory { RequestInvoiceForProcessingUseCase(schedulers = get(), gateway = get()) }
    factory { RequestInvoiceReturnUseCase(schedulers = get(), gateway = get()) }
}

val gatewaysModule: Module = module {
    single<InvoiceGateway> {
        InvoiceGatewayImpl(
            invoiceRepository = get(),
            invoiceMapper = InvoiceMapperToGatewayResult()
        )
    }
}

val repositoryModule: Module = module {
    single {
        InvoiceRepository(
            localDataSource = get(),
            remoteDataSource = get(),
            mapperToRepoResult = InvoiceMapperToRepoResult()
        )
    }
}

val dataSourceModule: Module = module {
    single { InvoiceLocalDataSource() }
}

val networkModule: Module = module {
    single { createInvoiceNetworkClient(InvoiceBackendSimulation.getBaseUrl(), BuildConfig.DEBUG) }
}

val commonModule: Module = module {
    single { SessionManager() }
    single<Schedulers> {
        object : Schedulers {
            override val subscribeOn: Scheduler
                get() = io.reactivex.schedulers.Schedulers.io()
            override val observeOn: Scheduler
                get() = io.reactivex.schedulers.Schedulers.io()
        }
    }
}

val demoModule: Module = module {
    single { DEMO_USER }
}