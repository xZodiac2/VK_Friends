package com.ilya.profileview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.profileViewDomain.User
import com.ilya.profileViewDomain.useCase.GetUserDataUseCase
import com.ilya.profileViewDomain.useCase.GetUserUseCaseData
import com.ilya.profileViewDomain.useCase.ResolveFriendRequestUseCase
import com.ilya.profileViewDomain.useCase.ResolveFriendRequestUseCaseData
import com.ilya.profileview.R
import com.ilya.profileview.presentation.screen.ErrorType
import com.ilya.profileview.presentation.screen.ProfileScreenEvent
import com.ilya.profileview.presentation.screen.ProfileScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val resolveFriendRequestUseCase: ResolveFriendRequestUseCase,
    private val accessTokenManager: AccessTokenManager
) : ViewModel() {

    private val _screenStateFlow = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Loading)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val getDataExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _screenStateFlow.value = when (throwable) {
            is UnknownHostException -> ProfileScreenState.Error(ErrorType.NoInternet)
            is SocketTimeoutException -> ProfileScreenState.Error(ErrorType.NoInternet)
            else -> ProfileScreenState.Error(ErrorType.Unknown(throwable))
        }
    }

    private val friendRequestExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _snackbarState.value = when (throwable) {
            is UnknownHostException -> SnackbarState.Triggered(
                text = StringResource.Resource(R.string.operation_not_completed)
            )

            is SocketTimeoutException -> SnackbarState.Triggered(
                text = StringResource.Resource(R.string.operation_not_completed)
            )

            else -> SnackbarState.Triggered(
                text = StringResource.Resource(R.string.operation_not_completed_try_later)
            )
        }
    }

    private var userId: Long = DEFAULT_USER_ID

    fun handleEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.Start -> {
                userId = event.userId
                onStart()
            }

            is ProfileScreenEvent.FriendRequest -> onFriendRequest(event.user)

            ProfileScreenEvent.Retry -> onRetry()
        }
    }

    private fun onFriendRequest(user: User) {
        viewModelScope.launch(Dispatchers.IO + friendRequestExceptionHandler) {
            val accessToken = accessTokenManager.accessToken

            if (accessToken == null) {
                _screenStateFlow.value = ProfileScreenState.Error(ErrorType.NoAccessToken)
                return@launch
            }

            val newFriendStatus = resolveFriendRequestUseCase(
                ResolveFriendRequestUseCaseData(
                    accessToken = accessToken.token,
                    user = user
                )
            )
            val state = _screenStateFlow.value as? ProfileScreenState.Success ?: return@launch

            _screenStateFlow.value = state.copy(
                user = state.user.copy(
                    friendStatus = newFriendStatus
                )
            )
        }
    }

    private fun onRetry() {
        _screenStateFlow.value = ProfileScreenState.Loading
        onStart()
    }

    private fun onStart() {
        viewModelScope.launch(Dispatchers.IO + getDataExceptionHandler) {
            val accessToken = accessTokenManager.accessToken

            if (accessToken == null) {
                _screenStateFlow.value = ProfileScreenState.Error(ErrorType.NoAccessToken)
                return@launch
            }

            val userData = getUserDataUseCase(
                GetUserUseCaseData(
                    accessToken = accessToken.token,
                    userId = userId
                )
            )

            _screenStateFlow.value = ProfileScreenState.Success(
                user = when {
                    userId == accessToken.userID -> userData.copy(isAccountOwner = true)
                    else -> userData
                }
            )
        }
    }

    companion object {
        private const val DEFAULT_USER_ID: Long = -1
    }

}