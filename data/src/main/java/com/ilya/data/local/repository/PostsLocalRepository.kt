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
        val sizes = photosWithSizes.flatMap { it.sizes }

        val videos = videosWithFirstFrames.map { it.video }
        val firstFrames = videosWithFirstFrames.flatMap { it.firstFrames }

        withTransaction {
            database.postsDao.upsertPostOwners(postOwner)
            database.postsDao.upsertPhotos(photos)
            database.postsDao.upsertAudios(audios)
            database.postsDao.upsertSizes(sizes)
            database.postsDao.upsertVideos(videos)
            database.postsDao.upsertFirstFrames(firstFrames)
            database.postsDao.upsertPosts(data)
            database.postsDao.upsertLikes(likes)
        }
    }

    override fun getPagingSource(): PagingSource<Int, PostWithAttachmentsAndOwner> {
        return database.postsDao.getPagingSource()
    }

    override suspend fun getAll(): List<PostWithAttachmentsAndOwner> {
        return database.postsDao.getAll()
    }

    override suspend fun deleteAll() {
        database.postsDao.deleteAll()
    }

    override suspend fun deleteAllWithPrimaryKeys() {
        database.postsDao.deleteAll()
        database.postsDao.deletePrimaryKeys()
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        database.withTransaction(block)
    }

}