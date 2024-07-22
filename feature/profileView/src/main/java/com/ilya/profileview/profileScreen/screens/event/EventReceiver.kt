package com.ilya.profileview.profileScreen.screens.event

import com.ilya.core.appCommon.EventHandler
import com.ilya.paging.Audio
import com.ilya.paging.Likes
import com.ilya.paging.Post
import com.ilya.paging.Video
import com.ilya.profileViewDomain.User

internal class EventReceiver(private val eventHandler: EventHandler<ProfileScreenEvent>) {

    fun onFriendRequest(user: User) {
        eventHandler.handleEvent(ProfileScreenEvent.FriendRequest(user))
    }

    fun onPhotoClick(userId: Long, targetPhotoIndex: Int) {
        eventHandler.handleEvent(
            ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.PhotoClick(userId, targetPhotoIndex))
        )
    }

    fun onOpenPhotosClick(userId: Long) {
        eventHandler.handleEvent(ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.OpenPhotosClick(userId)))
    }

    fun onLikeClick(post: Post) {
        eventHandler.handleEvent(ProfileScreenEvent.Like(post))
    }

    fun onPostPhotoClick(userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) {
        eventHandler.handleEvent(
            ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.PostPhotoClick(userId, targetPhotoIndex, photoIds))
        )
    }

    fun onAudioClick(audio: Audio) {
        eventHandler.handleEvent(ProfileScreenEvent.AudioClick(audio))
    }

    fun onVideoClick(video: Video) {
        eventHandler.handleEvent(
            ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.VideoClick(video.ownerId, video.id, video.accessKey))
        )
    }

    fun onEmptyAccessToken() {
        eventHandler.handleEvent(ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.EmptyAccessToken))
    }

    fun onPostAuthorClick(userId: Long, isPrivate: Boolean) {
        eventHandler.handleEvent(
            ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.PostAuthorClick(userId, isPrivate))
        )
    }

    fun onBackClick() {
        eventHandler.handleEvent(ProfileScreenEvent.Back)
        eventHandler.handleEvent(ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.BackClick))
    }

    fun onRetry() {
        eventHandler.handleEvent(ProfileScreenEvent.Retry)
    }

    fun onPostsAdded(newLikes: Map<Long, Likes>) {
        eventHandler.handleEvent(ProfileScreenEvent.PostsAdded(newLikes))
    }

    fun onSnackbarConsumed() {
        eventHandler.handleEvent(ProfileScreenEvent.SnackbarConsumed)
    }

    fun onStart(userId: Long) {
        eventHandler.handleEvent(ProfileScreenEvent.Start(userId))
    }

}