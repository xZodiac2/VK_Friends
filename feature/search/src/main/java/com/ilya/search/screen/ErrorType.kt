package com.ilya.search.screen


sealed interface ErrorType {
    object NoInternet : ErrorType
    object NoAccessToken : ErrorType
    data class Unknown(val error: Throwable) : ErrorType
}