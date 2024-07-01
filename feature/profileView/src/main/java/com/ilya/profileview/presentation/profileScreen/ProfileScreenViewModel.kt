package com.ilya.profileview.presentation.profileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.profileViewDomain.models.Post
import com.ilya.profileViewDomain.models.User
import com.ilya.profileViewDomain.useCase.GetPostsPagingFlowUseCase
import com.ilya.profileViewDomain.useCase.GetPostsPagingFlowUseCaseInvokeData
import com.ilya.profileViewDomain.useCase.GetUserDataUseCase
import com.ilya.profileViewDomain.useCase.GetUserUseCaseData
import com.ilya.profileViewDomain.useCase.ResolveFriendRequestUseCase
import com.ilya.profileViewDomain.useCase.ResolveFriendRequestUseCaseData
import com.ilya.profileview.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
internal class ProfileScreenViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val resolveFriendRequestUseCase: ResolveFriendRequestUseCase,
    private val accessTokenManager: AccessTokenManager,
    private val getPostsPagingFlowUseCase: GetPostsPagingFlowUseCase,
) : ViewModel() {

    private val userId = MutableStateFlow(DEFAULT_USER_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    val postsFlow: Flow<PagingData<Post>> = userId.flatMapLatest { id ->
        if (id == DEFAULT_USER_ID) {
            return@flatMapLatest flow { emit(PagingData.empty()) }
        }
        getPostsPagingFlowUseCase(
            GetPostsPagingFlowUseCaseInvokeData(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    initialLoadSize = INITIAL_LOAD_SIZE
                ),
                userId = id
            )
        )
    }.cachedIn(viewModelScope)

    private val _screenState = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val getDataExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _screenState.value = when (throwable) {
            is UnknownHostException -> ProfileScreenState.Error(ErrorType.NoInternet)
            is SocketTimeoutException -> ProfileScreenState.Error(ErrorType.NoInternet)
            else -> ProfileScreenState.Error(ErrorType.Unknown(throwable))
        }
    }

    private val friendRequestExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _snackbarState.value = when (throwable) {
            is UnknownHostException -> SnackbarState.Triggered(
                text = StringResource.FromId(R.string.operation_not_completed)
            )

            is SocketTimeoutException -> SnackbarState.Triggered(
                text = StringResource.FromId(R.string.operation_not_completed)
            )

            else -> SnackbarState.Triggered(
                text = StringResource.FromId(R.string.operation_not_completed_try_later)
            )
        }
    }

    fun handleEvent(event: ProfileScreenEvent) {
        when (event) {
            is ProfileScreenEvent.Start -> {
                userId.value = event.userId
                onStart()
            }

            is ProfileScreenEvent.FriendRequest -> onFriendRequest(event.user)
            ProfileScreenEvent.Retry -> onRetry()
            ProfileScreenEvent.SnackbarConsumed -> onSnackbarConsumed()
        }
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onFriendRequest(user: User) {
        viewModelScope.launch(Dispatchers.IO + friendRequestExceptionHandler) {
            val accessToken = accessTokenManager.accessToken

            if (accessToken == null) {
                _screenState.value = ProfileScreenState.Error(ErrorType.NoAccessToken)
                return@launch
            }

            val newFriendStatus = resolveFriendRequestUseCase(
                ResolveFriendRequestUseCaseData(
                    accessToken = accessToken.token,
                    user = user
                )
            )
            val state = _screenState.value as? ProfileScreenState.Success ?: return@launch

            _screenState.value = state.copy(
                user = state.user.copy(
                    friendStatus = newFriendStatus
                )
            )
        }
    }

    private fun onRetry() {
        _screenState.value = ProfileScreenState.Loading
        onStart()
    }

    private fun onStart() {
        viewModelScope.launch(Dispatchers.IO + getDataExceptionHandler) {
            val accessToken = accessTokenManager.accessToken

            if (accessToken == null) {
                _screenState.value = ProfileScreenState.Error(ErrorType.NoAccessToken)
                return@launch
            }

            val userData = getUserDataUseCase(
                GetUserUseCaseData(
                    accessToken = accessToken.token,
                    userId = userId.value
                )
            )

            _screenState.value = ProfileScreenState.Success(
                user = when {
                    userId.value == accessToken.userID -> userData.copy(isAccountOwner = true)
                    else -> userData
                }
            )
        }
    }

    companion object {
        private const val DEFAULT_USER_ID: Long = -1
        private const val PAGE_SIZE = 3
        private const val INITIAL_LOAD_SIZE = 6
    }

}