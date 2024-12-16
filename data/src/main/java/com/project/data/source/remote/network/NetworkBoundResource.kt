package com.project.data.source.remote.network

import com.project.common.utils.Resource
import kotlinx.coroutines.flow.Flow

abstract class NetworkBoundResource<ResultType, RequestType> {
    protected abstract suspend fun createCall() : ApiResponse<RequestType>
    abstract fun asFlow() : Flow<Resource<ResultType>>
}