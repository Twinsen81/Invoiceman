package com.evartem.invoiceman.di

import com.evartem.backendsim.InvoiceBackendSimulation
import com.evartem.data.gateway.InvoiceGatewayImpl
import com.evartem.data.gateway.mapper.InvoiceMapperToGatewayResult
import com.evartem.data.local.InvoiceLocalDataSource
import com.evartem.data.remote.api.InvoiceService
import com.evartem.data.remote.api.createInvoiceNetworkClient
import com.evartem.data.repository.InvoiceRepository
import com.evartem.data.repository.mapper.InvoiceMapperToRepoResult
import com.evartem.domain.gateway.InvoiceGateway
import com.evartem.domain.interactor.GetInvoicesForUserUseCase
import com.evartem.domain.interactor.Schedulers
import com.evartem.invoiceman.BuildConfig
import com.evartem.invoiceman.invoices.mvi.InvoicesViewModel
import com.evartem.invoiceman.util.DEMO_USER
import io.reactivex.Scheduler
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule: Module = module {
    viewModel { InvoicesViewModel(user = get(), getInvoicesForUserUseCase = get()) }
}

val useCasesModule: Module = module {
    factory { GetInvoicesForUserUseCase(schedulers = get(), gateway = get()) }
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
    single<InvoiceService> { createInvoiceNetworkClient(InvoiceBackendSimulation.getBaseUrl(), BuildConfig.DEBUG) }
}

val commonModule: Module = module {
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