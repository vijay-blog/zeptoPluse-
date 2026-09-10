package com.daily.nexamartpartner.core.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppFailure) : AppResult<Nothing>()
}
