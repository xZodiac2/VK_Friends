package com.ilya.core.appCommon

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vk.id.AccessToken
import javax.inject.Inject

class AccessTokenManager @Inject constructor(
    private val shPrefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<AccessToken>,
) {

    private val accessTokenOperationsListeners = mutableListOf<AccessTokenOperationsListener>()

    var accessToken: AccessToken? = getAccessTokenValue(notify = true)
        get() = getAccessTokenValue(notify = true)
        private set

    private fun getAccessTokenValue(notify: Boolean): AccessToken? {
        val accessTokenJsonString = shPrefs.getString(ACCESS_TOKEN_KEY, "") ?: ""
        if (notify) notifyListeners()
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

        notifyListeners()
    }

    fun clearToken() {
        with(shPrefs.edit()) {
            remove(ACCESS_TOKEN_KEY)
            apply()
        }
        notifyListeners()
    }

    private fun notifyListeners() {
        accessTokenOperationsListeners.forEach { listener ->
            listener.onOperation(getAccessTokenValue(notify = false))
        }
    }

    fun addAccessTokenListener(accessTokenOperationsListener: AccessTokenOperationsListener) {
        accessTokenOperationsListeners += accessTokenOperationsListener
    }

    fun removeAccessTokenListener(accessTokenOperationsListener: AccessTokenOperationsListener) {
        accessTokenOperationsListeners -= accessTokenOperationsListener
    }

    companion object {
        private const val ACCESS_TOKEN_KEY = "accessToken"
    }

}
