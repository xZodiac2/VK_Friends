package com.ilya.profileview.profileScreen.screens.event

sealed interface ProfileScreenNavEvent {
    data object EmptyAccessToken : ProfileScreenNavEvent
    data object BackClick : ProfileScreenNavEvent
    data class PhotoClick(val userId: Long, val targetPhotoIndex: Int) : ProfileScreenNavEvent
    data class OpenPhotosClick(val userId: Long) : ProfileScreenNavEvent
    data class PostPhotoClick(
        val userId: Long,
        val targetPhotoIndex: Int,
        val photoIds: Map<Long, String>
    ) : ProfileScreenNavEvent
    data class VideoClick(val userId: Long, val id: Long, val accessKey: String) : ProfileScreenNavEvent
    data class PostAuthorClick(val id: Long, val isPrivate: Boolean) : ProfileScreenNavEvent
}