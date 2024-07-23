package com.ilya.core.appCommon.base

interface EventHandler<in T> {
    fun handleEvent(event: T)
}