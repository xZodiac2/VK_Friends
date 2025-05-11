package com.ilya.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ilya.core.appCommon.StringResource
import com.ilya.core.appCommon.accessToken.AccessTokenManager
import com.ilya.core.appCommon.compose.basicComposables.snackbar.SnackbarState
import com.ilya.paging.mappers.toUser
import com.ilya.paging.models.User
import com.ilya.paging.pagingSources.UsersPagingSource
import com.ilya.search.screen.event.SearchScreenEvent
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
internal class SearchViewModel @Inject constructor(
  private val usersPagingSourceFactory: UsersPagingSource.Factory,
  private val accessTokenManager: AccessTokenManager
) : ViewModel() {

  private val searchValuesFlow = MutableSharedFlow<String>()

  @OptIn(ExperimentalCoroutinesApi::class)
  val usersFlow = searchValuesFlow
    .map(::newPager)
    .flatMapLatest { it.flow }
    .cachedIn(viewModelScope)

  private val _snackbarStateFlow = MutableStateFlow<SnackbarState>(SnackbarState.Consumed)
  val snackbarStateFlow = _snackbarStateFlow.asStateFlow()

  private val _accountOwnerStateFlow = MutableStateFlow<User?>(null)
  val accountOwnerStateFlow = _accountOwnerStateFlow.asStateFlow()

  init {
    viewModelScope.launch {
      delay(100)
      searchValuesFlow.emit("")
    }
  }

  private fun newPager(query: String): Pager<Int, User> {
    return Pager(
      config = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE
      ),
      pagingSourceFactory = { usersPagingSourceFactory.newInstance(query) }
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
    if (_accountOwnerStateFlow.value == null) {
      val accessTokenValue = accessTokenManager.accessToken ?: return
      _accountOwnerStateFlow.value = accessTokenValue.userData.toUser(accessTokenValue)
    }
  }

  private fun onSearch(query: String) {
    viewModelScope.launch {
      searchValuesFlow.emit(query)
    }
  }

  private fun onSnackbarConsumed() {
    _snackbarStateFlow.value = SnackbarState.Consumed
  }

  private fun onPlugAvatarClick() {
    _snackbarStateFlow.value =
      SnackbarState.Triggered(StringResource.FromId(R.string.data_not_loaded_yet))
  }

  companion object {
    const val PAGE_SIZE = 80
  }

}

