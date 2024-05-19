package com.ilya.profileview.presentation.screen

import com.ilya.profileViewDomain.User


sealed interface ProfileScreenState {
    data object Loading : ProfileScreenState
    data class Success(val user: User) : ProfileScreenState
    data class Error(val errorType: ErrorType) : ProfileScreenState
}

