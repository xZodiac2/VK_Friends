package com.ilya.core.appCommon

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


sealed interface StringResource {
    data class Str(val value: String) : StringResource
    data class FromId(
        @StringRes val id: Int,
        val formatArgs: List<Any> = emptyList()
    ) : StringResource
}


@Composable
fun StringResource.resolve(): String {
    return when (this) {
        is StringResource.Str -> value
        is StringResource.FromId -> stringResource(
            id = id, formatArgs = formatArgs.toTypedArray()
        )
    }
}