package com.ilya.core.appCommon.base

interface Toggleable<T> {
    fun toggle(): T
    fun last(): T
}