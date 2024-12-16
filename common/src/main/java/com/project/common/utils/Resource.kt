package com.project.common.utils

sealed class Resource<T>(val data: T? = null, val errorCode: Int? = null, val errorMessage: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(errorCode: Int, errorMessage: String, data: T? = null) : Resource<T>(data, errorCode, errorMessage)
}