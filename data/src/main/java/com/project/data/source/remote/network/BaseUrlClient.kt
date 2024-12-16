package com.project.data.source.remote.network

import com.project.common.network.UrlInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BaseUrlClient(urlInterface: UrlInterface, okHttpClient: OkHttpClient) :
    RetrofitClient<ApiServices> {
    override val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(urlInterface.baseUrl)
            .build()
    }
    override val services: ApiServices = retrofit.create(ApiServices::class.java)

}