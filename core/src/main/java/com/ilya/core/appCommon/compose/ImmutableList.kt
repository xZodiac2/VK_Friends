package com.ilya.core.appCommon.compose

import androidx.compose.runtime.Immutable

@Immutable
class ImmutableList<T>(val list: List<T>) : List<T> by list

fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> {
    return ImmutableList(this.toList())
}