package com.ilya.data.local.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.ilya.data.local.LocalRepository
import com.ilya.data.local.database.VkFriendsApplicationDatabase
import com.ilya.data.local.database.entities.PostWithAttachmentsAndOwner
import javax.inject.Inject

internal class PostsLocalRepository @Inject constructor(
    private val database: VkFriendsApplicationDatabase
) : LocalRepository<PostWithAttachmentsAndOwner> {

    override suspend fun upsertAll(vararg upsertData: PostWithAttachmentsAndOwner) {
        val data = upsertData.map { it.data }
        val photosWithSizes = upsertData.flatMap { it.photos }
        val videosWithFirstFrames = upsertData.flatMap { it.videos }
        val audios = upsertData.flatMap { it.audios }
        val postOwner = upsertData.map { it.owner }
        val likes = upsertData.map { it.likes }

        val photos = photosWithSizes.map { it.photo }
        val photoLikes = photosWithSizes.mapNotNull { it.likes }
        val sizes = photosWithSizes.flatMap { it.sizes }

        val videos = videosWithFirstFrames.map { it.video }
        val videoLikes = videosWithFirstFrames.mapNotNull { it.likes }
        val firstFrames = videosWithFirstFrames.flatMap { it.firstFrames }

        with(database) {
            withTransaction {
                postsDao.upsertPostOwners(postOwner)
                postsDao.upsertPhotos(photos)
                postsDao.upsertAudios(audios)
                postsDao.upsertSizes(sizes)
                postsDao.upsertVideos(videos)
                postsDao.upsertFirstFrames(firstFrames)
                postsDao.upsertPosts(data)
                postsDao.upsertPostLikes(likes)
                postsDao.upsertPhotoLikes(photoLikes)
                postsDao.upsertVideoLikes(videoLikes)
            }
        }
    }

    override fun getPagingSource(): PagingSource<Int, PostWithAttachmentsAndOwner> {
        return database.postsDao.getPagingSource()
    }

    override suspend fun getAll(): List<PostWithAttachmentsAndOwner> {
        return database.postsDao.getAll()
    }

    override suspend fun deleteAll() = with(database) {
        postsDao.deleteVideos()
        postsDao.deleteVideoLikes()
        postsDao.deleteFirstFrames()
        postsDao.deletePostLikes()
        postsDao.deletePhotos()
        postsDao.deletePhotoLikes()
        postsDao.deletePhotoSizes()
        postsDao.deleteAudios()
        postsDao.deleteOwners()
        postsDao.deletePosts()
        postsDao.deletePostPrimaryKeys()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        deleteAll()
        database.postsDao.deletePostPrimaryKeys()
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        database.withTransaction(block)
    }

}