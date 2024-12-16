package com.project.data.source.remote.network

import retrofit2.Retrofit

interface RetrofitClient<T> {
    val retrofit: Retrofit
    val services: T
}