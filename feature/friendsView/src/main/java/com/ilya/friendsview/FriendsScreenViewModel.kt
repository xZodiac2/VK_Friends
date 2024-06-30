package com.ilya.friendsview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.alertDialog.AlertDialogState
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.data.mappers.toUser
import com.ilya.data.paging.User
import com.ilya.data.paging.pagingSources.FriendsPagingSource
import com.ilya.friendsview.screen.FriendsScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FriendsScreenViewModel @Inject constructor(
    private val friendsPagingSource: FriendsPagingSource,
    private val accessTokenManager: AccessTokenManager,
) : ViewModel() {

    val pagingFlow = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { friendsPagingSource }
    ).flow
        .map { it.map { userDto -> userDto.toUser() } }
        .cachedIn(viewModelScope)

    private val _alertDialogState = MutableStateFlow<AlertDialogState>(AlertDialogState.Consumed)
    val alertDialogState = _alertDialogState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarState = _snackbarState.asStateFlow()

    private val _accountOwnerState = MutableStateFlow<User?>(null)
    val accountOwnerState = _accountOwnerState.asStateFlow()

    fun handleEvent(event: FriendsScreenEvent) {
        when (event) {
            FriendsScreenEvent.PlaceholderAvatarClick -> onPlaceholderAvatarClick()
            FriendsScreenEvent.SnackbarConsumed -> onSnackbarConsumed()
            FriendsScreenEvent.Start -> onStart()
            is FriendsScreenEvent.BackPress -> onBackPress(event.onConfirm)
        }
    }

    private fun onStart() {
        val accessTokenValue = accessTokenManager.accessToken ?: return
        _accountOwnerState.value = accessTokenValue.userData.toUser(accessTokenValue)
    }

    private fun onSnackbarConsumed() {
        _snackbarState.value = SnackbarState.Consumed
    }

    private fun onPlaceholderAvatarClick() {
        _snackbarState.value =
            SnackbarState.Triggered(StringResource.FromId(R.string.data_not_loaded_yet))
    }

    private fun onBackPress(onConfirm: () -> Unit) {
        _alertDialogState.value = AlertDialogState.Triggered(
            text = StringResource.FromId(R.string.app_exit_warning),
            onConfirm = {
                onConfirm()
                _alertDialogState.value = AlertDialogState.Consumed
            },
            onDismiss = {
                _alertDialogState.value = AlertDialogState.Consumed
            }
        )
    }

    companion object {
        const val PAGE_SIZE = 80
    }

}