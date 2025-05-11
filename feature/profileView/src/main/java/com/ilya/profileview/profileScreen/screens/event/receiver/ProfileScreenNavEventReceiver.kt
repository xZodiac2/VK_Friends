package com.ilya.profileview.profileScreen.screens.event.receiver

import com.ilya.core.appCommon.base.EventHandler
import com.ilya.paging.models.Audio
import com.ilya.paging.models.LikeableCommonInfo
import com.ilya.paging.models.Likes
import com.ilya.paging.models.Video
import com.ilya.profileViewDomain.User
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenEvent
import com.ilya.profileview.profileScreen.screens.event.ProfileScreenNavEvent

// this class created to simplify posting events from the UI to the view model
internal class EventReceiver(private val eventHandler: EventHandler<ProfileScreenEvent>) : ProfileScreenEventReceiver {

  override fun onFriendRequest(user: User) {
    eventHandler.handleEvent(ProfileScreenEvent.FriendRequest(user))
  }

  override fun onPhotoClick(userId: Long, targetPhotoIndex: Int) {
    eventHandler.handleEvent(
      ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.PhotoClick(userId, targetPhotoIndex))
    )
  }

  override fun onOpenPhotosClick(userId: Long) {
    eventHandler.handleEvent(ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.OpenPhotosClick(userId)))
  }

  override fun onLike(item: LikeableCommonInfo) {
    eventHandler.handleEvent(ProfileScreenEvent.Like(item))
  }

  override fun onPostPhotoClick(userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>) {
    eventHandler.handleEvent(
      ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.PostPhotoClick(userId, targetPhotoIndex, photoIds))
    )
  }

  override fun onAudioClick(audio: Audio) {
    eventHandler.handleEvent(ProfileScreenEvent.AudioClick(audio))
  }

  override fun onVideoClick(video: Video) {
    eventHandler.handleEvent(
      ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.VideoClick(video.ownerId, video.id, video.accessKey))
    )
  }

  override fun onEmptyAccessToken() {
    eventHandler.handleEvent(ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.EmptyAccessToken))
  }

  override fun onAnotherProfileClick(userId: Long, isPrivate: Boolean) {
    eventHandler.handleEvent(
      ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.AnotherProfileClick(userId, isPrivate))
    )
  }

  override fun onBackClick() {
    eventHandler.handleEvent(ProfileScreenEvent.Back)
    eventHandler.handleEvent(ProfileScreenEvent.NewNavEvent(ProfileScreenNavEvent.BackClick))
  }

  override fun onRetry() {
    eventHandler.handleEvent(ProfileScreenEvent.Retry)
  }

  override fun onPostAdded(newLikes: Map<Long, Likes>) {
    eventHandler.handleEvent(ProfileScreenEvent.PostsAdded(newLikes))
  }

  override fun onSnackbarConsumed() {
    eventHandler.handleEvent(ProfileScreenEvent.SnackbarConsumed)
  }

  override fun onStart(userId: Long) {
    eventHandler.handleEvent(ProfileScreenEvent.Start(userId))
  }

  override fun onCommentsClick(ownerId: Long) {
    eventHandler.handleEvent(ProfileScreenEvent.CommentsClick(ownerId))
  }

  override fun onDismissCommentsSheet() {
    eventHandler.handleEvent(ProfileScreenEvent.DismissBottomSheet)
  }

  override fun onCommentsAdded(newLikes: Map<Long, Likes>) {
    eventHandler.handleEvent(ProfileScreenEvent.CommentsAdded(newLikes))
  }

}