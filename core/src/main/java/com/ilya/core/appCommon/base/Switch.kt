package com.ilya.core.appCommon.base

class Switch<T>(
  private val first: T,
  private val second: T
) : MutableToggleable<T> {

  private var currentValue = first

  override fun toggle(): T {
    currentValue = if (currentValue === first) second else first
    return currentValue
  }

  override fun value(): T {
    return currentValue
  }

}
