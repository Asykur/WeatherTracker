package com.project.data.di

import com.project.common.utils.Environment
import com.project.data.repository.WeatherRepositoryImpl
import com.project.data.source.remote.RemoteDataSource
import com.project.data.source.remote.network.ApiServices
import com.project.data.source.remote.network.BaseUrlClient
import com.project.data.source.remote.network.RetrofitClient
import com.project.common.network.UrlInterface
import com.project.domain.repository.IWeatherRepository
import com.project.domain.repository.WeatherInterActor
import com.project.domain.usecase.WeatherUseCase
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val dataModule = module {
    single<UrlInterface> { Environment() }
    single { RemoteDataSource(get(), get()) }
    single<RetrofitClient<ApiServices>>(named("BaseUrlClient")) { BaseUrlClient(get(), get()) }
    single { provideOkHttpClient(get()) }
    single<IWeatherRepository> { WeatherRepositoryImpl(get()) }
    factory<WeatherUseCase> { WeatherInterActor(get()) }
    single { provideApiServices(get(named("BaseUrlClient"))) }
}


fun provideOkHttpClient(
    url: UrlInterface,
): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    val host = url.baseUrl.removePrefix("https://").removeSuffix("/")
    val certificatePinner = CertificatePinner.Builder()
    certificatePinner.add(host, )

    return OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .certificatePinner(certificatePinner.build())
        .build()
}

fun provideApiServices(
    retrofitClient: RetrofitClient<ApiServices>
): ApiServices = retrofitClient.services