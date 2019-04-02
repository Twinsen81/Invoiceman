package com.evartem.invoiceman

import android.app.Application
import com.evartem.backendsim.InvoiceBackendSimulation
import com.evartem.invoiceman.di.*
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class TheApp : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        Realm.init(this)
        // Clear the DB on the app start. Otherwise it becomes bloated with invoices since their IDs are generated
        // randomly by the InvoiceBackendSimulation
/*
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.deleteAll()
            }
        }
*/

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@TheApp)
            modules(
                viewModelModule,
                useCasesModule,
                gatewaysModule,
                repositoryModule,
                dataSourceModule,
                networkModule,
                commonModule,
                demoModule
            )
        }

        InvoiceBackendSimulation.startServer(7, 0)
    }

    companion object {
        private lateinit var INSTANCE: TheApp
        fun get() = INSTANCE
    }
}