package com.ilya.search.screen

internal sealed interface SearchScreenEvent {
    data object PlugAvatarClick : SearchScreenEvent
    data object SnackbarConsumed : SearchScreenEvent
    data object Start : SearchScreenEvent
    data class Search(val query: String) : SearchScreenEvent
}