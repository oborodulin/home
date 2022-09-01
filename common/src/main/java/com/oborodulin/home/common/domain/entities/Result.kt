package com.oborodulin.home.common.domain.entities

import com.oborodulin.home.common.domain.usecases.UseCaseException

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    class Error(val exception: UseCaseException) : Result<Nothing>()
}