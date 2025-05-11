package com.ilya.profileview.videoPreview

import com.ilya.paging.models.VideoExtended


internal sealed interface VideoPreviewEvent {
  data object SnackbarConsumed : VideoPreviewEvent
  data class Start(
    val ownerId: Long,
    val videoId: Long,
    val accessKey: String
  ) : VideoPreviewEvent

  data class Like(val video: VideoExtended?) : VideoPreviewEvent
}
