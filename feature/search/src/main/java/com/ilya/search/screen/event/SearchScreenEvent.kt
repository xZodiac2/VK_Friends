package com.ilya.search.screen.event

internal sealed interface SearchScreenEvent {
    data object PlugAvatarClick : SearchScreenEvent
    data object SnackbarConsumed : SearchScreenEvent
    data object Start : SearchScreenEvent
    data class Search(val query: String) : SearchScreenEvent
}