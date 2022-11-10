package com.oborodulin.home.common.domain.usecases

sealed class UseCaseException(cause: Throwable) : Throwable(cause) {

    class PayerException(cause: Throwable) : UseCaseException(cause)

    class ServiceException(cause: Throwable) : UseCaseException(cause)

    class UnknownException(cause: Throwable) : UseCaseException(cause)


    companion object {

        fun createFromThrowable(throwable: Throwable): UseCaseException {
            return if (throwable is UseCaseException) throwable else UnknownException(throwable)
        }
    }
}