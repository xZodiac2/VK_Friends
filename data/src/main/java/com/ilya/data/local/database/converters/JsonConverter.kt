package com.ilya.data.local.database.converters

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import java.io.IOException


internal abstract class JsonConverter<T>(private val jsonAdapter: JsonAdapter<T>) {

    @TypeConverter
    fun toJson(data: T?): String {
        return try {
            data?.let { jsonAdapter.toJson(it) } ?: ""
        } catch (e: IOException) {
            ""
        }
    }

    @TypeConverter
    fun fromJson(json: String): T? {
        return try {
            jsonAdapter.fromJson(json)
        } catch (e: IOException) {
            null
        }
    }

}