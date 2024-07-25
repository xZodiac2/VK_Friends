package com.ilya.friendsview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.basicComposables.alertDialog.AlertDialogState
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.friendsview.screen.event.FriendsScreenEvent
import com.ilya.paging.mappers.toUser
import com.ilya.paging.models.User
import com.ilya.paging.pagingSources.FriendsPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class FriendsScreenViewModel @Inject constructor(
    private val friendsPagingSourceFactory: FriendsPagingSource.Factory,
    private val accessTokenManager: AccessTokenManager,
) : ViewModel() {

    val pagingFlow = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { friendsPagingSourceFactory.newInstance(Unit) }
    ).flow.cachedIn(viewModelScope)

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