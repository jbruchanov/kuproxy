package com.scurab.kuproxy.util

import kotlinx.coroutines.CancellationException

inline fun <T, R> T.coRunCatching(block: T.() -> R): Result<R> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.Error(e)
        //swallow
    }
}

sealed class Result<T> {
    class Success<T>(val item: T) : Result<T>()
    class Error<T>(val error: Throwable) : Result<T>()

    val isSuccess get() = this is Success<*>
}
