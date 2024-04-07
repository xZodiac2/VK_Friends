package com.ilya.data.paging

sealed class PaginationError(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Throwable(message, cause) {

    object NoAccessToken : PaginationError() {
        private fun readResolve(): Any = NoAccessToken
    }

    object NoInternet : PaginationError() {
        private fun readResolve(): Any = NoInternet
    }
}
