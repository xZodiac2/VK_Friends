package com.ilya.friendsview.screen


internal sealed interface ErrorType {
  data object NoInternet : ErrorType
  data object NoAccessToken : ErrorType
  data class Unknown(val error: Throwable) : ErrorType
}