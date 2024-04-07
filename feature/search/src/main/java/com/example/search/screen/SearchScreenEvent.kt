package com.example.search.screen

sealed interface SearchScreenEvent {
    object PlugAvatarClick : SearchScreenEvent
    object SnackbarConsumed : SearchScreenEvent
    object Start : SearchScreenEvent
    data class Search(val query: String) : SearchScreenEvent
}