package com.ilya.data.local.database.converters

import androidx.room.ProvidedTypeConverter
import com.ilya.data.local.database.Attachments
import com.ilya.data.local.database.Likes
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

@ProvidedTypeConverter
internal class LikesConverter @Inject constructor(
    jsonAdapter: JsonAdapter<Likes>
) : JsonConverter<Likes>(jsonAdapter)

@ProvidedTypeConverter
internal class AttachmentsConverter @Inject constructor(
    jsonAdapter: JsonAdapter<Attachments>
) : JsonConverter<Attachments>(jsonAdapter)

