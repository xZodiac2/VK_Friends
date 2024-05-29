package com.ilya.data.local.database.converters

import androidx.room.ProvidedTypeConverter
import com.ilya.data.local.database.AttachmentsDatabaseDto
import com.ilya.data.local.database.LikesDatabaseDto
import com.ilya.data.local.database.PostOwnerDatabaseDto
import com.squareup.moshi.JsonAdapter
import javax.inject.Inject

@ProvidedTypeConverter
internal class LikesConverter @Inject constructor(
    jsonAdapter: JsonAdapter<LikesDatabaseDto>
) : JsonConverter<LikesDatabaseDto>(jsonAdapter)

@ProvidedTypeConverter
internal class AttachmentsConverter @Inject constructor(
    jsonAdapter: JsonAdapter<AttachmentsDatabaseDto>
) : JsonConverter<AttachmentsDatabaseDto>(jsonAdapter)

@ProvidedTypeConverter
internal class PostOwnerConverter @Inject constructor(
    jsonAdapter: JsonAdapter<PostOwnerDatabaseDto>
) : JsonConverter<PostOwnerDatabaseDto>(jsonAdapter)