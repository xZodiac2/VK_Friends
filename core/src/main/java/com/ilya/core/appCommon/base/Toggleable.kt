package com.ilya.core.appCommon.base

interface Toggleable<T> {
  fun toggled(): T
}

interface MutableToggleable<T> {
  fun toggle(): T
  fun value(): T
}
