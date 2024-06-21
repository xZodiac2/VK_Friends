package com.ilya.data.local.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ilya.data.local.database.entities.AudioEntity
import com.ilya.data.local.database.entities.FirstFrameEntity
import com.ilya.data.local.database.entities.PhotoEntity
import com.ilya.data.local.database.entities.PostLikesEntity
import com.ilya.data.local.database.entities.PostOwnerEntity
import com.ilya.data.local.database.entities.PostPagingEntity
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import com.ilya.data.local.database.entities.SizeEntity
import com.ilya.data.local.database.entities.VideoEntity

@Dao
internal interface PostsDao {

    @Upsert
    suspend fun upsertPhotos(photos: List<PhotoEntity>)

    @Upsert
    suspend fun upsertAudios(audios: List<AudioEntity>)

    @Upsert
    suspend fun upsertSizes(sizes: List<SizeEntity>)

    @Upsert
    suspend fun upsertLikes(likes: List<PostLikesEntity>)

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

    @Transaction
    @Query("DELETE FROM posts")
    suspend fun deleteAll()

    @Transaction
    @Query("DELETE FROM sqlite_sequence WHERE name = 'posts'")
    suspend fun deletePrimaryKeys()

}