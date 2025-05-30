package com.ilya.core.appCommon.accessToken

import android.content.SharedPreferences
import com.ilya.core.appCommon.base.BaseObservable
import com.squareup.moshi.Moshi
import com.vk.id.AccessToken
import okio.IOException
import javax.inject.Inject

class AccessTokenManager @Inject constructor(
  private val shPrefs: SharedPreferences,
  moshi: Moshi
) : BaseObservable<AccessTokenListener>() {

  private val jsonAdapter = moshi.adapter(AccessToken::class.java)

  var accessToken: AccessToken? = restoreToken()
    set(value) {
      field = value
      notifyListeners()
      updateToken(value)
    }

  private fun restoreToken(): AccessToken? {
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

  private fun updateToken(accessToken: AccessToken?) {
    if (accessToken == null) {
      with(shPrefs.edit()) {
        remove(ACCESS_TOKEN_KEY)
        apply()
      }
    } else {
      val accessTokenJsonString = jsonAdapter.toJson(accessToken)
      with(shPrefs.edit()) {
        putString(ACCESS_TOKEN_KEY, accessTokenJsonString)
        apply()
      }
    }
  }

  override fun notifyListeners() {
    synchronized(lock) {
      listeners.forEach { it.onChange(accessToken) }
    }
  }

  companion object {
    private const val ACCESS_TOKEN_KEY = "accessToken"
  }

}