package com.ilya.data.local.database.converters

import androidx.room.ProvidedTypeConverter
import com.ilya.data.local.database.Attachment
import com.ilya.data.local.database.Likes
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

@ProvidedTypeConverter
internal class LikesConverter @Inject constructor(
    override val jsonAdapter: JsonAdapter<Likes>
) : BaseConverter<Likes>()

@ProvidedTypeConverter
internal class AttachmentsConverter @Inject constructor(
    override val jsonAdapter: JsonAdapter<List<Attachment>>
) : BaseConverter<List<Attachment>>()

