package com.ilya.core.appCommon

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.vk.id.AccessToken
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import okio.IOException
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
class AccessTokenManager @Inject constructor(
    private val shPrefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<AccessToken>,
) : BaseObservable<AccessTokenListener>() {

    var accessToken: AccessToken? = getToken()
        get() = getToken()
        private set

    private fun getToken(): AccessToken? {
        val accessTokenJsonString = shPrefs.getString(ACCESS_TOKEN_KEY, "") ?: ""

        return try {
            when {
                accessTokenJsonString.isEmpty() -> null
                else -> jsonAdapter.fromJson(accessTokenJsonString)
            }
        } catch (e: IOException) {
            null
        }
    }

    fun saveAccessToken(accessToken: AccessToken) {
        this.accessToken = accessToken
        notifyListeners()
        saveTokenInPrefs(accessToken)
    }

    fun clearToken() {
        with(shPrefs.edit()) {
            remove(ACCESS_TOKEN_KEY)
            apply()
        }
        notifyListeners()
    }

    private fun saveTokenInPrefs(accessToken: AccessToken) {
        val jsonString = jsonAdapter.toJson(accessToken)

        with(shPrefs.edit()) {
            putString(ACCESS_TOKEN_KEY, jsonString)
            apply()
        }
    }


    override fun notifyListeners() {
        val accessToken = getToken()
        synchronized(mutex) {
            listeners.forEach { it.onChange(accessToken) }
        }
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "accessToken"
    }

}

