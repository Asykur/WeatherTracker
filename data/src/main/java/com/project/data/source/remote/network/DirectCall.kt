package com.project.data.source.remote.network

import com.project.common.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class DirectCall<ResultType, RequestType>(): NetworkBoundResource<ResultType, RequestType>() {
    private val result : Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        when(val response = createCall()){
            is ApiResponse.Success -> emit(Resource.Success(callResult(response.data)))
            is ApiResponse.Error -> emit(Resource.Error(response.errorCode, response.errorMessage))
        }
    }

    protected abstract suspend fun callResult(response: RequestType): ResultType

    override fun asFlow(): Flow<Resource<ResultType>> = result
}