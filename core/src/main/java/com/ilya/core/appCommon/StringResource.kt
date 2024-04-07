package com.ilya.core.appCommon

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


sealed interface StringResource {
    data class Str(val value: String) : StringResource
    data class Resource(
        @StringRes val id: Int,
        val arguments: List<Any> = emptyList()
    ) : StringResource
}


@Composable
fun StringResource.resolve(): String {
    return when (this) {
        is StringResource.Str -> value
        is StringResource.Resource -> stringResource(
            id = id, formatArgs = arguments.toTypedArray()
        )
    }
}