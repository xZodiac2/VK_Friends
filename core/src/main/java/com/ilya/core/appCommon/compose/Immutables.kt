package com.ilya.core.appCommon.compose

import androidx.compose.runtime.Immutable
import java.io.Serializable

@Immutable
class ImmutableList<T>(val list: List<T>) : List<T> by list

fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> {
  return ImmutableList(this.toList())
}

@Immutable
data class ImmutablePair<T, R>(val first: T, val second: R) : Serializable {
  override fun toString(): String = "($first, $second)"
}

infix fun <T, R> T.with(that: R) = ImmutablePair(this, that)
