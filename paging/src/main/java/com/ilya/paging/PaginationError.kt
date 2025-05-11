package com.ilya.paging

sealed class PaginationError(
  override val message: String? = null,
  override val cause: Throwable? = null
) : Throwable(message, cause) {

  data object NoAccessToken : PaginationError() {
    private fun readResolve(): Any = NoAccessToken
  }

  data object NoInternet : PaginationError() {
    private fun readResolve(): Any = NoInternet
  }
}
