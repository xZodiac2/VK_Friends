package com.ilya.profileview.presentation.profileScreen.states

import com.ilya.profileViewDomain.models.User
import com.ilya.profileview.presentation.profileScreen.ErrorType


sealed interface ProfileScreenState {
    data object Loading : ProfileScreenState
    data class Success(val user: User) : ProfileScreenState
    data class Error(val errorType: ErrorType) : ProfileScreenState
}

