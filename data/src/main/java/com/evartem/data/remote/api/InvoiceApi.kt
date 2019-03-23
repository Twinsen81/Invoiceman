package com.evartem.data.remote.api

import com.evartem.data.BuildConfig
import com.evartem.data.remote.model.InvoiceRemoteModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class InvoiceApi(baseUrl: String) : InvoiceService {

    private val service: InvoiceService

    init {

        val httpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging: HttpLoggingInterceptor
            logging = HttpLoggingInterceptor { message -> Timber.tag("SERVER-BODY: ").d(message) }
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(logging)
        }

        val httpClient = httpClientBuilder.build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()

        service = retrofit.create(InvoiceService::class.java)
    }

    override fun getInvoicesForUser(userId: String): Single<Response<List<InvoiceRemoteModel>>> =
        service.getInvoicesForUser(userId)
}