package com.ilya.profileview.profileScreen.screens.event.receiver

import com.ilya.paging.models.Audio
import com.ilya.paging.models.LikeableCommonInfo
import com.ilya.paging.models.Likes
import com.ilya.paging.models.Video
import com.ilya.profileViewDomain.User


internal interface ProfileScreenEventReceiver {
  // Base events
  fun onStart(userId: Long)
  fun onSnackbarConsumed()
  fun onEmptyAccessToken()

  // Events on user interactions
  fun onOpenPhotosClick(userId: Long)
  fun onRetry()
  fun onPhotoClick(userId: Long, targetPhotoIndex: Int)
  fun onPostPhotoClick(userId: Long, targetPhotoIndex: Int, photoIds: Map<Long, String>)
  fun onAudioClick(audio: Audio)
  fun onVideoClick(video: Video)
  fun onAnotherProfileClick(userId: Long, isPrivate: Boolean)
  fun onLike(item: LikeableCommonInfo)
  fun onBackClick()
  fun onFriendRequest(user: User)
  fun onDismissCommentsSheet()
  fun onCommentsClick(ownerId: Long)

  // Events on fetch new data
  fun onPostAdded(newLikes: Map<Long, Likes>)
  fun onCommentsAdded(newLikes: Map<Long, Likes>)
}
