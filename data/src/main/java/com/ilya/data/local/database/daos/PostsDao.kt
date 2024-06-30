package com.ilya.data.local.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.PhotoEntity
import com.ilya.data.local.database.entities.PhotoLikesEntity
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostPagingEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.VideoEntity
import com.ilya.data.local.database.entities.VideoLikesEntity

@Dao
internal interface PostsDao {

    @Upsert
    suspend fun upsertPhotos(photos: List<PhotoEntity>)

    @Upsert
    suspend fun upsertAudios(audios: List<AudioEntity>)

    @Upsert
    suspend fun upsertSizes(sizes: List<SizeEntity>)

    @Upsert
    suspend fun upsertPostLikes(likes: List<PostLikesEntity>)

    @Upsert
    suspend fun upsertPhotoLikes(likes: List<PhotoLikesEntity>)

    @Upsert
    suspend fun upsertVideoLikes(likes: List<VideoLikesEntity>)

    @Upsert
    suspend fun upsertFirstFrames(firstFrames: List<FirstFrameEntity>)

    @Upsert
    suspend fun upsertPostOwners(postOwners: List<PostOwnerEntity>)

    @Upsert
    suspend fun upsertVideos(videos: List<VideoEntity>)

    @Upsert
    suspend fun upsertPosts(posts: List<PostPagingEntity>)

    @Transaction
    @Query("SELECT * FROM posts ORDER BY pagingId")
    fun getPagingSource(): PagingSource<Int, PostWithAttachmentsAndOwner>

    @Transaction
    @Query("SELECT * FROM posts")
    suspend fun getAll(): List<PostWithAttachmentsAndOwner>

    @Query("DELETE FROM posts")
    suspend fun deletePosts()

    @Query("DELETE FROM post_owners")
    suspend fun deleteOwners()

    @Query("DELETE FROM post_likes")
    suspend fun deletePostLikes()

    @Query("DELETE FROM photos")
    suspend fun deletePhotos()

    @Query("DELETE FROM photo_likes")
    suspend fun deletePhotoLikes()

    @Query("DELETE FROM photo_sizes")
    suspend fun deletePhotoSizes()

    @Query("DELETE FROM videos")
    suspend fun deleteVideos()

    @Query("DELETE FROM video_likes")
    suspend fun deleteVideoLikes()

    @Query("DELETE FROM audios")
    suspend fun deleteAudios()

    @Query("DELETE FROM first_frames")
    suspend fun deleteFirstFrames()

    @Query("DELETE FROM sqlite_sequence WHERE name = 'posts'")
    suspend fun deletePostPrimaryKeys()

}