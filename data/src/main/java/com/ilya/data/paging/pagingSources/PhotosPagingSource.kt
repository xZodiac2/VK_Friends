package com.ilya.data.paging.pagingSources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilya.core.appCommon.AccessTokenManager
import com.ilya.core.appCommon.BaseFactory
import com.ilya.core.util.logThrowable
import com.ilya.data.paging.PaginationError
import com.ilya.data.remote.UserDataRemoteRepository
import com.ilya.data.remote.retrofit.api.dto.PhotoDto
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class PhotosPagingSource private constructor(
    private val remoteRepository: UserDataRemoteRepository,
    private val accessTokenManager: AccessTokenManager,
    private val userId: Long,
    private val isPreview: Boolean
) : PagingSource<Int, PhotoDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoDto> {
        try {
            val key = params.key ?: 0
            val offset = key * params.loadSize

            val accessToken = accessTokenManager.accessToken ?: return LoadResult.Error(
                PaginationError.NoAccessToken
            )

            val photos = remoteRepository.getPhotos(
                accessToken = accessToken.token,
                ownerId = userId,
                extended = isPreview,
                offset = offset,
                count = params.loadSize
            )

            val prevKey = if (key == 0) {
                null
            } else {
                (key - 1).coerceAtLeast(0)
            }
            val nextKey = key + 1

            val itemsAfter = (photos.count - nextKey * params.loadSize).coerceAtLeast(0)
            val itemsBefore = (photos.count - itemsAfter - photos.items.size).coerceAtLeast(0)

            return LoadResult.Page(
                data = photos.items,
                prevKey = if (photos.items.isEmpty()) null else prevKey,
                nextKey = if (photos.items.isEmpty()) null else nextKey,
                itemsBefore = itemsBefore,
                itemsAfter = itemsAfter
            )

        } catch (e: SocketTimeoutException) {
            logThrowable(e)
            return LoadResult.Error(PaginationError.NoInternet)
        } catch (e: UnknownHostException) {
            logThrowable(e)
            return LoadResult.Error(PaginationError.NoInternet)
        } catch (e: Exception) {
            logThrowable(e)
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    class Factory @Inject constructor(
        private val remoteRepository: UserDataRemoteRepository,
        private val accessTokenManager: AccessTokenManager,
    ) : BaseFactory<InitData, PhotosPagingSource> {
        override fun newInstance(initializationData: InitData): PhotosPagingSource {
            return PhotosPagingSource(
                remoteRepository = remoteRepository,
                accessTokenManager = accessTokenManager,
                userId = initializationData.userId,
                isPreview = initializationData.isPreview
            )
        }
    }

    data class InitData(
        val userId: Long,
        val isPreview: Boolean
    )

}