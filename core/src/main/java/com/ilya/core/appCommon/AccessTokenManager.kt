package com.ilya.core.appCommon

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.vk.id.AccessToken
import javax.inject.Inject

class AccessTokenManager @Inject constructor(
    private val shPrefs: SharedPreferences,
    private val jsonAdapter: JsonAdapter<AccessToken>,
) {
    
    var accessToken: AccessToken? = getAccessTokenValue()
        get() = getAccessTokenValue()
        private set
    
    private fun getAccessTokenValue(): AccessToken? {
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
    }
    
    fun clearToken() {
        with(shPrefs.edit()) {
            clear()
            apply()
        }
    }
    
    companion object {
        private const val ACCESS_TOKEN_KEY = "accessToken"
        const val DEFAULT_USER_ID: Long = -1
    }
    
}