package com.ilya.search.screen.event

sealed interface SearchScreenNavEvent {
    data object EmptyAccessToken : SearchScreenNavEvent
    data class ProfileClick(val id: Long, val isPrivate: Boolean) : SearchScreenNavEvent
}