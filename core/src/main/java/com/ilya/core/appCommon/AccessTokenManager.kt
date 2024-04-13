package com.ilya.core.appCommon

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.vk.id.AccessToken
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
class AccessTokenManager @Inject constructor(
    private val shPrefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<AccessToken>,
) : BaseObservable<AccessTokenOperationsListener>() {

    var accessToken: AccessToken? = getAccessTokenValueNotifying()
        get() = getAccessTokenValueNotifying()
        private set

    private fun getAccessTokenValueNotifying(): AccessToken? {
        notifyObservers()
        return getToken()
    }

    private fun getAccessTokenValue(): AccessToken? {
        return getToken()
    }

    private fun getToken(): AccessToken? {
        val accessTokenJsonString = shPrefs.getString(ACCESS_TOKEN_KEY, "") ?: ""

        return when {
            accessTokenJsonString.isEmpty() -> null
            else -> jsonAdapter.fromJson(accessTokenJsonString)
        }
    }

    fun saveAccessToken(accessToken: AccessToken) {
        val jsonString = jsonAdapter.toJson(accessToken)

        with(shPrefs.edit()) {
            putString(ACCESS_TOKEN_KEY, jsonString)
            apply()
        }

        notifyObservers()
    }

    fun clearToken() {
        with(shPrefs.edit()) {
            remove(ACCESS_TOKEN_KEY)
            apply()
        }
        notifyObservers()
    }

    override fun notifyObservers() {
        val accessToken = getAccessTokenValue()
        synchronized(mutex) {
            observers.forEach { it.onOperation(accessToken) }
        }
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "accessToken"
    }

}

