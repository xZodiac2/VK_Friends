package com.ilya.profileview.presentation.profileScreen

import com.ilya.profileViewDomain.models.User


internal sealed interface ProfileScreenState {
    data object Loading : ProfileScreenState
    data class Success(val user: User) : ProfileScreenState
    data class Error(val errorType: ErrorType) : ProfileScreenState
}

