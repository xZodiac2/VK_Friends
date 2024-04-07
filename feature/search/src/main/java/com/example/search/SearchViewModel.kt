package com.example.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.search.screen.SearchScreenEvent
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.StringResource
import com.ilya.core.basicComposables.snackbar.SnackbarState
import com.ilya.data.local.database.UserEntity
import com.ilya.data.paging.User
import com.ilya.data.paging.pagingSources.UsersPagingSourceFactory
import com.ilya.data.paging.remoteMediators.UsersRemoteMediator
import com.ilya.data.toUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val usersRemoteMediatorFactory: UsersRemoteMediator.Factory,
    private val pagingSourceFactory: UsersPagingSourceFactory,
    private val accessTokenManager: AccessTokenManager
) : ViewModel() {

    private val searchValueSharedFlow = MutableSharedFlow<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingFlow = searchValueSharedFlow
        .map(::newPager)
        .flatMapLatest { it.flow }
        .map { data -> data.map { userEntity -> userEntity.toUser() } }
        .cachedIn(viewModelScope)

    private val _snackbarStateFlow = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
    val snackbarStateFlow = _snackbarStateFlow.asStateFlow()

    private val _accountOwnerStateFlow = MutableStateFlow<User?>(null)
    val accountOwnerStateFlow = _accountOwnerStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            delay(100)
            searchValueSharedFlow.emit("")
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun newPager(query: String): Pager<Int, UserEntity> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
            remoteMediator = usersRemoteMediatorFactory.newInstance(query),
            pagingSourceFactory = { pagingSourceFactory.newInstance(Unit) }
        )
    }

    fun handleEvent(event: SearchScreenEvent) {
        when (event) {
            SearchScreenEvent.PlugAvatarClick -> onPlugAvatarClick()
            SearchScreenEvent.SnackbarConsumed -> onSnackbarConsumed()
            SearchScreenEvent.Start -> onStart()
            is SearchScreenEvent.Search -> onSearch(event.query)
        }
    }

    private fun onStart() {
        val accessTokenValue = accessTokenManager.accessToken ?: return
        _accountOwnerStateFlow.value = accessTokenValue.userData.toUser(accessTokenValue)
    }

    private fun onSearch(query: String) {
        viewModelScope.launch {
            searchValueSharedFlow.emit(query)
        }
    }

    private fun onSnackbarConsumed() {
        _snackbarStateFlow.value = SnackbarState.Consumed
    }

    private fun onPlugAvatarClick() {
        _snackbarStateFlow.value =
            SnackbarState.Triggered(StringResource.Resource(R.string.data_not_loaded_yet))
    }

    companion object {
        const val PAGE_SIZE = 80
    }

}

