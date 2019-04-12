package com.evartem.data.remote.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Create the Retrofit implementation of the [InvoiceService] interface.
 *
 * @param baseUrl the URL of the REST server
 * @param debug enable BODY-level logging
 */
fun createInvoiceNetworkClient(baseUrl: String, debug: Boolean): InvoiceService =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(createHttpClient(debug))
        .build()
        .create(InvoiceService::class.java)

fun createHttpClient(debug: Boolean, connectTimeout: Long = 5, readTimeout: Long = 10): OkHttpClient {
    val httpClientBuilder = OkHttpClient.Builder()
        .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        .readTimeout(readTimeout, TimeUnit.SECONDS)

    if (debug) {
        val logging: HttpLoggingInterceptor
        logging = HttpLoggingInterceptor { message -> Timber.tag("SERVER-BODY: ").d(message) }
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.addInterceptor(logging)
    }

    return httpClientBuilder.build()
}

fun createMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()