package com.ilya.friendsview

import com.ilya.data.User

sealed interface FriendsScreenState {
    object Loading : FriendsScreenState
    data class Error(val error: ErrorType) : FriendsScreenState
    data class Success(val friends: List<User>) : FriendsScreenState
}

sealed interface ErrorType {
    object NoInternet : ErrorType
    object NoAccessToken : ErrorType
}